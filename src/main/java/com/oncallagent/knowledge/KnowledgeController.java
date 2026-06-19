package com.oncallagent.knowledge;

import com.oncallagent.common.ApiResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 知识文档上传 + 检索接口。 */
@RestController
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeRetriever knowledgeRetriever;

    public KnowledgeController(
            KnowledgeService knowledgeService, KnowledgeRetriever knowledgeRetriever) {
        this.knowledgeService = knowledgeService;
        this.knowledgeRetriever = knowledgeRetriever;
    }

    /** 上传一篇文档,读取文本后摄取入向量库。 */
    @PostMapping("/api/documents")
    public ApiResponse<IngestResult> upload(@RequestParam("file") MultipartFile file)
            throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        String source = file.getOriginalFilename();
        int chunks = knowledgeService.ingest(text, source);
        return ApiResponse.ok(new IngestResult(source, chunks));
    }

    /** 按相似度检索知识块(用于验证检索效果)。 */
    @GetMapping("/api/documents/search")
    public ApiResponse<List<Match>> search(@RequestParam("q") String query) {
        List<Match> matches =
                knowledgeRetriever.retrieve(query).stream().map(this::toMatch).toList();
        return ApiResponse.ok(matches);
    }

    private Match toMatch(Document d) {
        return new Match((String) d.getMetadata().get("source"), d.getScore(), d.getText());
    }

    /** 摄取结果。 */
    public record IngestResult(String source, int chunks) {}

    /** 检索命中。 */
    public record Match(String source, Double score, String text) {}
}

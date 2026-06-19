package com.oncallagent.knowledge;

import com.oncallagent.common.ApiResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 知识文档上传接口。 */
@RestController
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
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

    /** 摄取结果。 */
    public record IngestResult(String source, int chunks) {}
}

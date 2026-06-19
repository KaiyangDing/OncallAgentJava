package com.oncallagent.knowledge;

import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

/** 知识摄取:把文档文本切块,交给 VectorStore(自动嵌入并入库)。 */
@Service
public class KnowledgeService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter = new TokenTextSplitter();

    public KnowledgeService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * 摄取一篇文档:切块 → 嵌入 → 入库。
     *
     * @param text 文档全文
     * @param source 来源标识(如文件名),存为元数据,便于日后溯源
     * @return 入库的切块数量
     */
    public int ingest(String text, String source) {
        Document doc = new Document(text, Map.<String, Object>of("source", source));
        List<Document> chunks = splitter.split(List.of(doc));
        vectorStore.delete(new FilterExpressionBuilder().eq("source", source).build());
        vectorStore.add(chunks);
        return chunks.size();
    }
}

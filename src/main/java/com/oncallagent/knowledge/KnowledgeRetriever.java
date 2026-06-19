package com.oncallagent.knowledge;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/** 知识检索:按相似度查 top-K,过滤掉低于阈值的结果。 */
@Service
public class KnowledgeRetriever {

    private final VectorStore vectorStore;
    private final KnowledgeProperties properties;

    public KnowledgeRetriever(VectorStore vectorStore, KnowledgeProperties properties) {
        this.vectorStore = vectorStore;
        this.properties = properties;
    }

    /** 检索与 query 相关的知识块;全部低于阈值则返回空列表。 */
    public List<Document> retrieve(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(properties.topK())
                        .similarityThreshold(properties.similarityThreshold())
                        .build());
    }
}

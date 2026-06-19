package com.oncallagent.knowledge;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** 知识检索可调参数,从 application.yaml 的 oncall.knowledge.* 绑定。 */
@ConfigurationProperties(prefix = "oncall.knowledge")
public record KnowledgeProperties(
        @DefaultValue("4") int topK, @DefaultValue("0.5") double similarityThreshold) {}

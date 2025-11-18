package com.zion.learning.subject.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@Document(collection = "subjects")
public class Subject extends BaseEntity {
    private String title;           // 科目名称
    private String coverImage;      // 封面图
    private Long userId;            // 用户ID
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long tagId;             // 标签ID
    
    private Long stageCount;     // 阶段总数
    private Long knowledgePointCount; // 知识点总数
    private BigDecimal masteryRate; // 掌握程度
}
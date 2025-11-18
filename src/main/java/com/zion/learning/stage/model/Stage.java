package com.zion.learning.stage.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@Document(collection = "stages")
public class Stage extends BaseEntity {
    private String title;           // 阶段名称
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;         // 所属科目ID
    
    private Integer orderNum;       // 阶段序号
    private Long knowledgePointCount; // 知识点数量
    private BigDecimal masteryRate; // 掌握程度
}
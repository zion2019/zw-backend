package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "breakdowns")
public class Breakdown extends BaseEntity {
    private String title;           // 拆解点名称
    private String content;         // 内容（富文本）
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long knowledgePointId;  // 所属知识点ID
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;         // 所属科目ID
}
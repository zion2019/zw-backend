package com.zion.learning.knowledge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Document(collection = "knowledge_points")
public class KnowledgePoint extends BaseEntity {
    private String title;           // 知识点名称
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long stageId;           // 所属阶段ID
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;         // 所属科目ID
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;            // 用户ID
    
    private Integer breakdownCount; // 拆解点数量

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long strategyId;        // 学习策略ID

    private Long currStrategyIntervalId; // 所处的学习策略间隔ID

    private LocalDateTime nextReviewTime; // 下次复习时间

    private Integer masteryLevelCode;// 掌握程度code
}
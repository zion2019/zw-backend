package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import com.zion.learning.common.constents.MasteryLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "learning_intervals")
public class LearningStrategyInterval extends BaseEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long strategyId;        // 关联的学习策略ID
    
    private Integer intervalHours;   // 距离上一个interval的复习时间（小时）
    
    private MasteryLevel requiredMasteryLevel; // 当前interval所需的掌握程度
    
    private Integer sequence;        // 序号，用于确定interval的顺序
}
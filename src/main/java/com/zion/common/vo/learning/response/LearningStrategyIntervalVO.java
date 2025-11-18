package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.learning.common.constents.MasteryLevel;
import lombok.Data;

import java.io.Serializable;

@Data
public class LearningStrategyIntervalVO implements Serializable {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long strategyId;        // 关联的学习策略ID
    
    private Integer intervalHours;   // 距离上一个interval的复习时间（小时）
    
    private MasteryLevel requiredMasteryLevel; // 当前interval所需的掌握程度
    
    private Integer sequence;        // 序号，用于确定interval的顺序
}
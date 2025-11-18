package com.zion.learning.strategy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "learning_strategies")
public class LearningStrategy extends BaseEntity {
    private String name;            // 策略名称
    private String description;     // 描述
    private Boolean allowFallback;  // 是否允许回退
    private Boolean isSystemDefault; // 是否系统预设
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;            // 用户ID（null为系统策略）
}
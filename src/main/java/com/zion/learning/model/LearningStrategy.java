package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "learning_strategies")
public class LearningStrategy extends BaseEntity {
    private String name;            // 策略名称
    private String description;     // 描述
    private Boolean isDefault;      // 是否为默认策略
    private Boolean allowFallback;  // 是否允许回退

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;            // 用户ID（null为系统策略）
}
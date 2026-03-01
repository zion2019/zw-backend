package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Data;

import java.util.List;

@Data
public class LearningStrategyQO extends Page {
    private Long id;
    private String name;
    private String description;
    private List<LearningStrategyIntervalQO> intervals;
    private Boolean allowFallback;
    private Boolean isDefault;
    private Long userId;
}
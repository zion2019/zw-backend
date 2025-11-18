package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LearningStrategyVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    private String name;
    
    private String description;
    
    private List<LearningStrategyIntervalVO> intervals;
    
    private Boolean allowFallback;
    
    private Boolean isSystemDefault;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}
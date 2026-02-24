package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SubjectVO implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    private String title;
    
    private String coverImage;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long tagId;
    
    private Integer stageCount;
    
    private Integer knowledgePointCount;

    /**
     * 已延期天数
     */
    private Long delayDays;


}
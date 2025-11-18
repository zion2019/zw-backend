package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class BreakdownVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    private String title;
    
    private String content;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long knowledgePointId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;
}
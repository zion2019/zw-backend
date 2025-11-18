package com.zion.common.vo.learning.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.Page;
import com.zion.learning.common.constents.PracticeResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PracticeRecordQO extends Page implements Serializable {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long knowledgePointId;
    
    private PracticeResult result;
    
    private LocalDateTime practiceStartTime;
    
    private LocalDateTime practiceEndTime;
}
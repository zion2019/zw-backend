package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.learning.common.constents.PracticeResult;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PracticeRecordVO implements Serializable {

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
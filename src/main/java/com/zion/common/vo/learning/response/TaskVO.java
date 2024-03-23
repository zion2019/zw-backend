package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TaskVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long topicId;

    private String title;

    private String content;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean routine;

    private LocalDateTime actualCloseTime;

    private String closeRemark;

    private Boolean finished;

    private String routineCron;

}

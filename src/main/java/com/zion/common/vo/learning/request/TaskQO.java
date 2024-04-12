package com.zion.common.vo.learning.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class TaskQO {

    private Long id;

    private String title;

    private String content;

    private Long topicId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime taskTime;

    private boolean routine;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualCloseTime;

    private Integer remindTimeType;

    private Integer remindTimeNum;

    private LocalDateTime remindTime;

    private String routineCron;

}

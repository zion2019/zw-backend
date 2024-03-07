package com.zion.common.vo.learning.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskQO {

    private Long id;

    private String title;

    private String description;

    private Long topicId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime actualCloseTime;

    private String dailyCron;

}

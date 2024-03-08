package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class TodoTodayVO implements Serializable {
    private Long taskId;

    private String title;

    private Long remainingHour;

    private String topicFullName;
}

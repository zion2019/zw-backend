package com.zion.common.vo.learning.response;

import lombok.Builder;
import lombok.Data;

@Data
public class TodoTodayVO {
    private Long taskId;

    private String title;

    private Long remainingHour;

    private String topicFullName;
}

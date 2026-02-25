package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PracticeQO extends Page {
    private Long id;

    private Long userId;

    private Long topicId;

    private Long pointId;

    private LocalDateTime practiseDate;

    private Integer intervalDays;
}

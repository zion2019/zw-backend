package com.zion.common.vo.learning.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.Page;
import com.zion.learning.common.PractiseResult;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Data
public class PracticeQO extends Page {
    private Long id;

    private Long userId;

    private Long topicId;

    private Long pointId;

    private LocalDateTime practiseDate;

    private PractiseResult result;

    private Integer intervalDays;
}

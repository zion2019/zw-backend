package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TodoTaskVO implements Serializable {
    private Long taskId;

    private String title;

    private BigDecimal remainingHour;

    private String topicFullName;

    public String background;

    public BigDecimal completePercent;
}

package com.zion.common.vo.learning.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskDelayQO implements Serializable {

    private Long userId;

    private Long taskId;

    private String delayReason;

    private Integer delayTimeType;

    private Integer delayTimeNum;
}

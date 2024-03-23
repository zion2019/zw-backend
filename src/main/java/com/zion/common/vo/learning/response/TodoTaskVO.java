package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TodoTaskVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long taskId;

    private String title;

    private String topicFullName;

    /**
     * 截止时间
     */
    private TaskExpireTagVo expireTag;

    public String background;
}

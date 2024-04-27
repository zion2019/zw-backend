package com.zion.common.vo.learning.request;

import com.zion.learning.common.TaskStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskFinishQO implements Serializable {

    private Long userId;

    private Long taskId;

    private String finishRemark;

    private TaskStatus taskStatus;
}

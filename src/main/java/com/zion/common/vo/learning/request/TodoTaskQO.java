package com.zion.common.vo.learning.request;

import com.zion.common.basic.BasicPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TodoTaskQO extends BasicPageRequest implements Serializable {

    private Long userId;

    private boolean today;

    private Long topicId;

}

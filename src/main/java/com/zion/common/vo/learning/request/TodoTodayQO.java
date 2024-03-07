package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TodoTodayQO  extends Page implements Serializable {

    private Long userId;
}

package com.zion.common.basic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicPageRequest {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private Long total;
}

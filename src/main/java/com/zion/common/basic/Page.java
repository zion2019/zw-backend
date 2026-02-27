package com.zion.common.basic;

import cn.hutool.core.collection.ListUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page<T> implements Serializable {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private Long total = 0L;

    private List<T> dataList = ListUtil.empty();

    public Page(Integer pageNo,Integer pageSize) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

     public Page() {}
}

package com.zion.common.basic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page<T> implements Serializable {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    private Long total;

    private List<T> dataList;

    public Page(Integer pageNo,Integer pageSize) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

     public Page() {}
}

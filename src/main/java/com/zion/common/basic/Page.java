package com.zion.common.basic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> implements Serializable {

    private Integer pageNo = 10;
    private Integer pageSize = 10;

    private Long total;

    private List<T> dataList;

    public Page(Integer pageNo,Integer pageSize) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }
}

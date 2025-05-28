package com.zion.common.vo.bill.req;

import com.zion.common.basic.Page;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BillQO extends Page implements Serializable{

    private Long id;

    private Long userId;

    private BigDecimal amount;

    private Long categoryId;

    private String remark;

    private String location;

    private List<Long> categoryIdList;

    private String startDay;

    private String endDay;
}

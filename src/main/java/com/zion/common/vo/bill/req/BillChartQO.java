package com.zion.common.vo.bill.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class BillChartQO implements Serializable {
    private Long userId;
    private String startDay;
    private String endDay;
    private Long billCategoryPId;
}

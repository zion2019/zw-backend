package com.zion.common.vo.bill.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BillChartVO implements Serializable {

    private String name ;

    private BigDecimal value;

    private String color;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long categoryId;
}

package com.zion.common.vo.bill.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BillsVO implements Serializable {

    private BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long categoryId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String categoryDesc;
    private String categoryColor;
    private Integer categoryType;


    private String billDate;
    private String billRemark;

    private String location;
}

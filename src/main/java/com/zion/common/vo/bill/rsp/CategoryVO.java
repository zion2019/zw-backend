package com.zion.common.vo.bill.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CategoryVO implements Serializable {

    /**
     * 分类ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 分类类型
     * com.zion.bill.constans.CategoryType
     */
    private Integer categoryType;

    /**
     * title
     */
    private String title;
    /**
     * 颜色
     */
    private String color;
    /**
     * 父ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;
    /**
     * 编码
     */
    private String code;

    /**
     * 账单金额
     */
    private BigDecimal billAmount = BigDecimal.ZERO;

    /**
     * 全ID路径
     */
    private String fullPath;
}

package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ChartDataVO implements Serializable {
    /**
     * 日期
     */
    private Date date;
    
    /**
     * 复习总量
     */
    private Integer totalReviews;
    
    /**
     * 记得量
     */
    private Integer rememberCount;
    
    /**
     * 记得率
     */
    private BigDecimal rememberRate;
}
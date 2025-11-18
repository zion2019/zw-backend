package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class KeyIndicatorVO implements Serializable {
    /**
     * 本月记得率
     */
    private BigDecimal currentMonthRememberRate;
    
    /**
     * 本月记得量
     */
    private Integer currentMonthRememberCount;
    
    /**
     * 本月复习总量
     */
    private Integer currentMonthTotalReviews;
    
    /**
     * 最近30天记得率
     */
    private BigDecimal recent30DaysRememberRate;
    
    /**
     * 最近30天记得量
     */
    private Integer recent30DaysRememberCount;
    
    /**
     * 最近30天复习总量
     */
    private Integer recent30DaysTotalReviews;
}
package com.zion.common.vo.bill.rsp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillsExcelVO {
    @ExcelProperty("ID")
    private String id;

    @ExcelProperty("创建时间")
    private LocalDateTime createdTime;

    @ExcelProperty("创建人")
    private String createdUser;

    @ExcelProperty("更新时间")
    private LocalDateTime updatedTime;

    @ExcelProperty("更新人")
    private String updatedUser;

    @ExcelProperty("版本号")
    private Integer version;

    @ExcelProperty("是否删除")
    private Integer deleted;

    @ExcelProperty("用户ID")
    private String userId;

    @ExcelProperty("金额")
    private BigDecimal amount;

    @ExcelProperty("分类ID")
    private String categoryId;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("位置")
    private String location;
}

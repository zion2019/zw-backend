package com.zion.common.vo.bill.rsp;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryExcelVO {

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

    @ExcelProperty("分类名称")
    private String title;

    @ExcelProperty("编码")
    private String code;

    @ExcelProperty("颜色")
    private String color;

    @ExcelProperty("所属分类ID")
    private String parentId;

    @ExcelProperty("分类等级")
    private Integer level;

    /**
     * 分类类型
     * com.zion.bill.constans.CategoryType
     */
    @ExcelProperty("分类类型")
    private Integer categoryType;

    @ExcelProperty("分类全ID路径")
    private String fullPath;

    @ExcelProperty("父类全路径")
    private String fullParentName;



}

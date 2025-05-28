package com.zion.common.vo.bill.req;

import com.zion.common.basic.Page;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryQO extends Page {
    private Long id;

    private Long userId;

    private String title;

    private String code;

    private String color;

    private Long parentId;

    private Long excludeId;

    private List<Long> ids;

    /**
     * 分类类型
     * com.zion.bill.constans.CategoryType
     */
    private Integer categoryType;


    /**
     * need to statistics bills
     */
    private Boolean stats = false;

    /**
     * statistics bills start time
     */
    private String statsBillSTime;
    /**
     * statistics bills end time
     */
    private String statsBillETime;


}

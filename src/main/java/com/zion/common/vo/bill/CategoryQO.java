package com.zion.common.vo.bill;

import com.zion.common.basic.Page;
import lombok.Builder;
import lombok.Data;

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

    /**
     * needing query the full path Name When fullPath is true.
     */
    private Boolean fullPath;

}

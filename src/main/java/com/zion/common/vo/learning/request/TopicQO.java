package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Builder
@Data
public class TopicQO extends Page {
    private Long id;
    private Long userId;

    private String title;

    private String code;

    private String background;

    private Long parentId;

    private Long excludeId;

    /**
     * needing query the full path Name When fullPath is true.
     */
    private Boolean fullPath;



}

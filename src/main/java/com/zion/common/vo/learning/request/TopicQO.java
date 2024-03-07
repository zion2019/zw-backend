package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TopicQO extends Page {
    private Long id;
    private Long userId;

    private String title;

    private String background;

    private Long parentId;

    private Long excludeId;

    /**
     * needing query the full path Name When fullPath is true.
     */
    private Boolean fullPath;
}

package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Data;

@Data
public class SubjectQO extends Page {
    private Long id;
    private String title;
    private Long tagId;
    private Long userId;

    /**
     * 阶段数
     */
    private Long stageCount;

    /**
     * 知识点数
     */
    private Long knowledgePointCount;
}
package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Data;

@Data
public class StageQO extends Page {
    private Long id;
    private String title;
    private Long subjectId;
    private Integer orderNum;
    private Long knowledgePointCount;
    private Long userId;
}
package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Data;

@Data
public class BreakdownQO extends Page {
    private Long id;
    private String title;
    private String content;
    private Long knowledgePointId;
    private Long subjectId;
    private Long userId;
}
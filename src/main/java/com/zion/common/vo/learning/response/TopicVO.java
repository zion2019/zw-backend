package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TopicVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String title;

    private String background;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;

    private List tree;

    private Long weight;

    private String fullParentName;

    private String fullTitle;

    private TopicStatisticVo statistic;
}

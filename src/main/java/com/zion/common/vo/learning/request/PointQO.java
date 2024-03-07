package com.zion.common.vo.learning.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.Page;
import com.zion.common.vo.resource.request.SubPointQO;
import com.zion.learning.common.DegreeOfMastery;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
public class PointQO extends Page implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String title;

    private DegreeOfMastery degreeOfMastery;

    private Integer subPointCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long topicId;

    private List<SubPointQO> subPoints;

    private Long userId;
}

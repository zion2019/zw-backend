package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.learning.common.DegreeOfMastery;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PointVo implements Serializable
{

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String title;

    private DegreeOfMastery degreeOfMastery;

    private Integer subPointCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long topicId;

    private List<SubPointVo> subPoints;
}

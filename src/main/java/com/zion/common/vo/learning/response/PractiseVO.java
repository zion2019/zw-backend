package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.learning.common.DegreeOfMastery;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PractiseVO implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long topicId;

    public String title;

    public String fullTitle;

    public String background;

    private DegreeOfMastery degreeOfMastery;

    public BigDecimal toDayDoneCount;

    public BigDecimal undoCount;

    public BigDecimal toDayTotalCount;

    public BigDecimal toDayCompletePercent;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Long pointId;

    public PointVo point;
}

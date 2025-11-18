package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StageVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;

    private Integer orderNum;

    private Integer knowledgePointCount;

    private BigDecimal masteryRate;
}
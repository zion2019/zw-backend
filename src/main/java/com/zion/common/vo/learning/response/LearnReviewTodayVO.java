package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LearnReviewTodayVO implements Serializable {
    /**
     * 待复习的科目
     */
    private List<SubjectVO> reviewSubjects;
}

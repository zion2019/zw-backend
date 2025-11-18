package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuickReviewStatusVO implements Serializable {
    private Boolean hasTasks;
    private String message;
}
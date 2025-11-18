package com.zion.common.vo.learning.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class HomeFunctionVO implements Serializable {
    private String name;
    private String icon;
    private String path;
}
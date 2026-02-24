package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Data;

@Data
public class TagQO extends Page {
    private Long id;
    private String name;
    private String color;
    private String description;
    private Long userId;
}
package com.zion.common.vo.learning.request;

import com.zion.common.basic.Page;
import lombok.Data;

import java.util.List;

@Data
public class TagQO extends Page {
    private Long id;
    private List<Long> queryIds;
    private String name;
    private String color;
    private String description;
    private Long userId;
}
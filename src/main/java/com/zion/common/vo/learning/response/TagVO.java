package com.zion.common.vo.learning.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class TagVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    private String name;
    
    private String color;
    
    private String icon;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;
    
    private Integer sortOrder;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}
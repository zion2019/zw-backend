package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "tags")
public class Tag extends BaseEntity {
    private String name;            // 标签名称
    private String color;           // 颜色
    private String icon;            // 图标
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long parentId;          // 父标签ID
    
    private Integer sortOrder;      // 排序权重
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;            // 用户ID
}
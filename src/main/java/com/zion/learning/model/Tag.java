package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "tags")
public class Tag extends BaseEntity {
    private String name;            // 标签名称
    private String color;           // 颜色
    private String description;            // 描述
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;            // 用户ID
}
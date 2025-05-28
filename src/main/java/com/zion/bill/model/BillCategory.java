package com.zion.bill.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Builder
@Document(collection = "bill_category")
public class BillCategory extends BaseEntity {

    private Long userId;

    private String title;

    private String code;

    private String color;

    private Long parentId;

    private Integer level;

    /**
     * 分类类型
     * com.zion.bill.constans.CategoryType
     */
    private Integer categoryType;

    private String fullPath;

    private String fullParentName;

    /**
     * Aka count of needed to learn.
     */
    private Long weight;

    @JsonIgnore
    private Long excludeId;

    @JsonIgnore
    private Long parentIdLike;


    @JsonIgnore
    private Set<String> topicCodes;

}

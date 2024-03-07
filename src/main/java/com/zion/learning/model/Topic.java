package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "topics")
public class Topic  extends BaseEntity {

    private Long userId;

    private String title;

    private String background;

    private Long parentId;

    private Integer level;

    private String fullPath;

    private String fullParentName;

    /**
     * Aka count of needed to learn.
     */
    private Long weight;

    @JsonIgnore
    private Long excludeId;

}

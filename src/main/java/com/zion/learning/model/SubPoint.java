package com.zion.learning.model;

import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "sub_points")
public class SubPoint extends BaseEntity {

    private Long pointId;

    private String title;

    private String detailContent;

}

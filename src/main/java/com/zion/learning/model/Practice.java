package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zion.common.basic.BaseEntity;
import com.zion.learning.common.PractiseResult;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
@Data
@Document(collection = "practices")
public class Practice extends BaseEntity {

    private Long userId;

    private Long topicId;

    private Long pointId;

    private LocalDateTime practiseDate;

    private PractiseResult result;

    @JsonIgnore
    private LocalDateTime gtePractiseDate;

    @JsonIgnore
    private LocalDateTime ltePractiseDate;

    @JsonIgnore
    private List<Long> topicIds;
}

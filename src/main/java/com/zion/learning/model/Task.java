package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Document(collection = "tasks")
public class Task  extends BaseEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long topicId;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime actualCloseTime;

    private String closeRemark;

    private Boolean finished;

    private String dailyCron;

}

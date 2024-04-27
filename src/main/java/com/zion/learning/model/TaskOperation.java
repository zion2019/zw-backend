package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Document(collection = "task_operation")
public class TaskOperation extends BaseEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long taskId;

    private Integer type;

    private String remark;


}

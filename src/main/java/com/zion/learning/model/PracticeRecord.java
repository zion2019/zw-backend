package com.zion.learning.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zion.common.basic.BaseEntity;
import com.zion.learning.common.constents.PracticeResult;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Document(collection = "practice_records")
public class PracticeRecord extends BaseEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;            // 用户ID
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long subjectId;         // 科目ID
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long knowledgePointId;  // 知识点ID
    
    private PracticeResult result;  // 练习结果


    private LocalDateTime practiceStartTime; // 练习开始时间

    private LocalDateTime practiceEndTime;
}
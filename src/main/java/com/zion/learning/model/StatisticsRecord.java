package com.zion.learning.model;

import com.zion.common.basic.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "statistics_records")
public class StatisticsRecord extends BaseEntity {
    private Long userId;
    private String statisticType; // 统计类型
    private Object statisticData; // 统计数据
    private LocalDateTime recordDate; // 记录日期
}
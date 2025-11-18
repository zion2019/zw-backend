package com.zion.learning.statistics.model;

import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "statistics_records")
public class StatisticsRecord extends BaseEntity {
    private Long userId;
    private String statisticType; // 统计类型
    private Object statisticData; // 统计数据
    private LocalDateTime recordDate; // 记录日期
}
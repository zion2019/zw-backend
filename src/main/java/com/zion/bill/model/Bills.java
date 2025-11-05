package com.zion.bill.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "bills")
public class Bills extends BaseEntity {
    private Long userId;

    private BigDecimal amount;

    private Long categoryId;

    private Long channelId;

    private String remark;

    private String location;

    @JsonIgnore
    private LocalDateTime queryBillStartTime;
    @JsonIgnore
    private LocalDateTime queryBillEndTime;
    @JsonIgnore
    private List<Long> queryCategoryIdList;
}

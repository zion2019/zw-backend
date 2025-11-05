package com.zion.bill.model;

import com.zion.common.basic.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "bill_channels")
public class BillChannel extends BaseEntity {
    private Long userId;
    
    private String name;
    
    private String description;
    
    private Integer status;
}
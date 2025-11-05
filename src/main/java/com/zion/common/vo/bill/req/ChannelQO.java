package com.zion.common.vo.bill.req;

import com.zion.common.basic.Page;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelQO extends Page implements Serializable {
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String description;
    
    private Integer status;
}
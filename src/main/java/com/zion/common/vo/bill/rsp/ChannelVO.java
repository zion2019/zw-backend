package com.zion.common.vo.bill.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelVO implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    private String name;
    
    private String description;
    
    private Integer status;
}
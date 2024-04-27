package com.zion.learning.common;

import lombok.Getter;

@Getter
public enum TaskStatus {

    READING(0,"READING")
    ,FINISHED(1,"FINISHED")
    ,CLOSED(2,"CLOSED")
    ;

    private final Integer code;
    private final String des;

    TaskStatus(Integer code,String des){
        this.code = code;
        this.des = des;
    }
}

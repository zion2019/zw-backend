package com.zion.learning.common;

import lombok.Getter;

/**
 * all operation type maintenance
 */
@Getter
public enum TaskOperationType {

    NEW(0,"New Task")
    ,EDIT(1,"Edit Task")
    ,DELAY(2,"Delay Task")
    ,FINISH(3,"Finish Task")
    ,AUTO_FINISH(4,"Auto Finish Task")
    ;

    private final Integer code;
    private final String des;

    TaskOperationType(Integer code,String des){
        this.code = code;
        this.des = des;
    }
}

package com.zion.learning.common;

import lombok.Getter;

import java.time.temporal.ChronoUnit;

/**
 * remind type
 */
@Getter
public enum TaskTimeType {
    HOUR(0,"UNDERSTAND", ChronoUnit.HOURS)
    ,MINUTE(1,"UNDERSTAND",ChronoUnit.MINUTES)
    ,DAY(2,"UNDERSTAND",ChronoUnit.DAYS)
    ;

    final Integer code;
    final String des;
    final ChronoUnit chronoUnit;

    TaskTimeType(Integer code, String des, ChronoUnit chronoUnit){
        this.code = code;
        this.des = des;
        this.chronoUnit = chronoUnit;
    }

    public static TaskTimeType getType(Integer code){
        if(code == null){
            return MINUTE;
        }
        for (TaskTimeType taskTimeType : TaskTimeType.values()) {
            if(taskTimeType.code.equals(code)){
                return taskTimeType;
            }
        }
        return MINUTE;
    }
}

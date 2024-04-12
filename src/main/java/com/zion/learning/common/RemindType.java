package com.zion.learning.common;

import lombok.Getter;

import java.time.temporal.ChronoUnit;

/**
 * remind type
 */
@Getter
public enum RemindType {
    HOUR(0,"UNDERSTAND", ChronoUnit.HOURS)
    ,MINUTE(1,"UNDERSTAND",ChronoUnit.MINUTES)
    ,DAY(2,"UNDERSTAND",ChronoUnit.DAYS)
    ;

    final Integer code;
    final String des;
    final ChronoUnit chronoUnit;

    RemindType(Integer code, String des,ChronoUnit chronoUnit){
        this.code = code;
        this.des = des;
        this.chronoUnit = chronoUnit;
    }

    public static RemindType getType(Integer code){
        if(code == null){
            return MINUTE;
        }
        for (RemindType remindType : RemindType.values()) {
            if(remindType.code.equals(code)){
                return remindType;
            }
        }
        return MINUTE;
    }
}

package com.zion.learning.common;

import lombok.Getter;

/**
 * 掌握程度
 */
@Getter
public enum DegreeOfMastery {
    UNDERSTAND(0,"UNDERSTAND",1)
    ,UNDERSTAND_AGAIN(1,"UNDERSTAND",3)
    ,FAMILIAR(2,"UNDERSTAND",5)
    ,FAMILIAR_AGAIN(3,"UNDERSTAND",10)
    ,PER_MASTER(4,"UNDERSTAND",20)
    ,MASTER(5,"UNDERSTAND",30)
    ;

    Integer code;
    String des;
    Integer intervalDay;

    DegreeOfMastery(Integer code,String des,Integer intervalDay){
        this.code = code;
        this.des = des;
        this.intervalDay = intervalDay;
    }

    public static DegreeOfMastery upgrade(DegreeOfMastery currentDegree){
        if(currentDegree == null || currentDegree.equals(DegreeOfMastery.MASTER)){
            return null;
        }
        for (DegreeOfMastery mastery : DegreeOfMastery.values()) {
            if(mastery.code.equals(currentDegree.code+1)){
                return mastery;
            }
        }
        return null;
    }
}

package com.zion.learning.common;

/**
 * 掌握程度
 */
public enum PractiseResult {
    UNDO(0,"UNDO"),
    FORGET(-1,"FORGET"),
    DONE(1,"DONE")
    ;

    Integer code;
    String des;

    PractiseResult(Integer code, String des){
        this.code = code;
        this.des = des;
    }
}

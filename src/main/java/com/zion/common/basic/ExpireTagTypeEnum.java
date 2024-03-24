package com.zion.common.basic;

/**
 * expire tag enum
 */
public enum ExpireTagTypeEnum {

    primary("primary")
    ,success("success")
    ,info("info")
    ,warning("warning")
    ,danger("danger");

    private String code;
    ExpireTagTypeEnum(String code){
        this.code = code;
    }


}

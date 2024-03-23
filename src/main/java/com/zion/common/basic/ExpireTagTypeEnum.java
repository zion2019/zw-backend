package com.zion.common.basic;

/**
 * expire tag enum
 */
public enum ExpireTagTypeEnum {

    primary("primary")
    ,SUCCESS("success")
    ,INFO("info")
    ,WARNING("warning")
    ,DANGER("danger");

    private String code;
    ExpireTagTypeEnum(String code){
        this.code = code;
    }


}

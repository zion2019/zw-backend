package com.zion.bill.constants;

public enum CategoryType {
    IN(1),
    OUT(0)
    ;
    private Integer type;

    CategoryType(Integer type) {
        this.type = type;
    }
    public Integer getType() {
        return type;
    }
}

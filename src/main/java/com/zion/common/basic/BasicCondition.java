package com.zion.common.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
public abstract class BasicCondition {

    @JsonIgnore
    private List<Long> ids;

    @JsonIgnore
    private String[] includeFields;

    @JsonIgnore
    private String sortFiledName;

    @JsonIgnore
    private Sort.Direction sortDirection;

    @JsonIgnore
    private Boolean isSort;


    public void include(String... includeFields){
        this.includeFields = includeFields;
    }

    public void ids(List<Long> ids){
        this.ids = ids;
    }

    public void sort(String sortFiledName,Sort.Direction sortDirection){
        this.sortFiledName = sortFiledName;
        this.sortDirection = sortDirection;
        this.isSort = true;
    }
}



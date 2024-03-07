package com.zion.common.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
public abstract class BasicCondition<T> {

    @JsonIgnore
    private List<Long> ids;

    @JsonIgnore
    private String[] includeFields;

    @JsonIgnore
    private String sortFiledName;

    @JsonIgnore
    private Sort.Direction sortDirection;
    @JsonIgnore
    private boolean isSort;


    public void include(String... includeFields){
        this.includeFields = includeFields;
    }

    public void ids(List<Long> ids){
        this.ids = ids;
    }

    public void sort(String sortFiledName,Sort.Direction sortDirection){
        this.sortFiledName = sortFiledName;
        this.sortDirection = sortDirection;
        isSort = true;
    }
}



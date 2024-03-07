package com.zion.common.basic;

import lombok.Data;

import java.util.List;

@Data
public class TreeVo {

    private String label;

    private List<TreeVo> children;

}

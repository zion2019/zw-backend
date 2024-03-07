package com.zion.common.vo.resource.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubPointQO {

    private Long id;

    private Long pointId;

    private String title;

    private String detailContent;
}

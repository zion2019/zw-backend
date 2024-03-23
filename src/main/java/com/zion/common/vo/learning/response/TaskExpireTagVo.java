package com.zion.common.vo.learning.response;

import com.zion.common.basic.ExpireTagTypeEnum;
import lombok.Data;

@Data
public class TaskExpireTagVo {

    private ExpireTagTypeEnum tagType;

    private String tagName;
}

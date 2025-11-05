package com.zion.common.vo.bill.rsp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 最近使用信息
 */
@Data
public class BillRecentInfoVO implements Serializable {

    /**
     * 最近使用的分类信息
     */
    private List<CategoryVO> categories;

    /**
     * 最近使用的渠道信息
     */
    private List<ChannelVO> channels;
}

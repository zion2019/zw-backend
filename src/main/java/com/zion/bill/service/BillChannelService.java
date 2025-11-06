package com.zion.bill.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.bill.req.ChannelQO;
import com.zion.common.vo.bill.rsp.ChannelVO;

import java.util.List;

public interface BillChannelService {
    Long save(ChannelQO qo);
    
    void delete(Long id, Long userId);
    
    ChannelVO info(Long id, Long userId);
    
    Page<ChannelVO> page(ChannelQO qo);
    
    List<ChannelVO> condition(ChannelQO qo);
}
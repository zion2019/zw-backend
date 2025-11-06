package com.zion.bill.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import com.zion.bill.dao.BillChannelDao;
import com.zion.bill.model.BillChannel;
import com.zion.bill.service.BillChannelService;
import com.zion.common.basic.Page;
import com.zion.common.vo.bill.req.ChannelQO;
import com.zion.common.vo.bill.rsp.ChannelVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillChannelServiceImpl implements BillChannelService {

    @Resource
    private BillChannelDao billChannelDao;

    @Override
    public Long save(ChannelQO qo) {
        Assert.isTrue(qo.getUserId() != null, "userId is required");
        Assert.isTrue(qo.getName() != null && !qo.getName().trim().isEmpty(), "name is required");
        
        BillChannel channel = null;
        if (qo.getId() != null) {
            channel = billChannelDao.getById(qo.getId());
            Assert.isTrue(channel != null && channel.getUserId().equals(qo.getUserId()), "渠道不存在或无权操作");
        } else {
            channel = BillChannel.builder().build();
        }
        
        channel.setName(qo.getName().trim());
        channel.setDescription(qo.getDescription());
        channel.setStatus(qo.getStatus() != null ? qo.getStatus() : 1); // 默认启用
        channel.setUserId(qo.getUserId());
        
        billChannelDao.save(channel);

        return channel.getId();
    }

    @Override
    public void delete(Long id, Long userId) {
        Assert.isTrue(id != null, "id is required");
        Assert.isTrue(userId != null, "userId is required");
        
        BillChannel channel = billChannelDao.getById(id);
        Assert.isTrue(channel != null && channel.getUserId().equals(userId), "渠道不存在或无权操作");
        
        billChannelDao.remove(channel);
    }

    @Override
    public ChannelVO info(Long id, Long userId) {
        Assert.isTrue(id != null, "id is required");
        Assert.isTrue(userId != null, "userId is required");
        
        BillChannel channel = billChannelDao.getById(id);
        if (channel == null || !channel.getUserId().equals(userId)) {
            return null;
        }
        
        return convertToVO(channel);
    }

    @Override
    public Page<ChannelVO> page(ChannelQO qo) {
        Assert.isTrue(qo.getUserId() != null, "userId is required");
        
        BillChannel condition = BillChannel.builder()
                .userId(qo.getUserId())
                .name(qo.getName())
                .status(qo.getStatus())
                .build();
        
        condition.sort("createdTime", Sort.Direction.DESC);
        
        Page<BillChannel> channelPage = billChannelDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), BillChannel.class, condition);
        
        Page<ChannelVO> pageRes = new Page<>();
        pageRes.setPageNo(channelPage.getPageNo());
        pageRes.setPageSize(channelPage.getPageSize());
        pageRes.setTotal(channelPage.getTotal());
        
        if (CollUtil.isNotEmpty(channelPage.getDataList())) {
            List<ChannelVO> channelVOS = channelPage.getDataList().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
            pageRes.setDataList(channelVOS);
        }
        
        return pageRes;
    }

    @Override
    public List<ChannelVO> condition(ChannelQO qo) {
        BillChannel condition = BillChannel.builder()
                .userId(qo.getUserId())
                .name(qo.getName())
                .status(qo.getStatus())
                .build();
        
        condition.sort("createdTime", Sort.Direction.DESC);
        
        List<BillChannel> channels = billChannelDao.condition(condition);
        
        if (CollUtil.isEmpty(channels)) {
            return ListUtil.empty();
        }
        
        return channels.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private ChannelVO convertToVO(BillChannel channel) {
        ChannelVO vo = new ChannelVO();
        vo.setId(channel.getId());
        vo.setName(channel.getName());
        vo.setDescription(channel.getDescription());
        vo.setStatus(channel.getStatus());
        return vo;
    }
}
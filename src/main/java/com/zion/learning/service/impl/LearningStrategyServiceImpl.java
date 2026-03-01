package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.zion.common.basic.BaseEntity;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.db.ZCondition;
import com.zion.common.db.ZOrder;
import com.zion.common.db.ZUpdateCondition;
import com.zion.common.vo.learning.request.LearningStrategyIntervalQO;
import com.zion.common.vo.learning.request.LearningStrategyQO;
import com.zion.common.vo.learning.response.LearnStrategyNextVO;
import com.zion.common.vo.learning.response.LearningStrategyIntervalVO;
import com.zion.common.vo.learning.response.LearningStrategyVO;
import com.zion.learning.mapper.LearningStrategyIntervalMapper;
import com.zion.learning.mapper.LearningStrategyMapper;
import com.zion.learning.service.LearningStrategyService;
import com.zion.learning.model.LearningStrategy;
import com.zion.learning.dao.LearningStrategyDao;
import com.zion.learning.model.LearningStrategyInterval;
import com.zion.learning.dao.LearningStrategyIntervalDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LearningStrategyServiceImpl implements LearningStrategyService {
    
    @Resource
    private LearningStrategyDao learningStrategyDao;
    
    @Resource
    private LearningStrategyIntervalDao learningStrategyIntervalDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(LearningStrategyQO qo) {
        LearningStrategy entity = LearningStrategyMapper.INSTANCE.toEntity(qo);
        learningStrategyDao.save(entity);

        // 处理默认策略逻辑
        if (Boolean.TRUE.equals(qo.getIsDefault())) {
            this.setDefaultStrategy(entity.getId(), qo.getUserId());
        }

        // save intervals
        this.saveIntervals(entity.getId(), qo.getIntervals());
        return true;
    }
    
    private void saveIntervals(Long strategyId, List<LearningStrategyIntervalQO> intervals) {
        List<LearningStrategyInterval> newIntervals = new ArrayList<>(intervals.size());
        intervals.forEach(i -> newIntervals.add(LearningStrategyIntervalMapper.INSTANCE.toEntity(i)));

        // 先删除原有的间隔设置
        learningStrategyIntervalDao.delete(new ZCondition<LearningStrategyInterval>()
                .eq(LearningStrategyInterval::getStrategyId, strategyId));

        // 保存新的间隔设置
        if (CollUtil.isNotEmpty(newIntervals)) {
            newIntervals.forEach(interval -> {
                interval.setStrategyId(strategyId);
                learningStrategyIntervalDao.save(interval);
            });
        }
    }
    
    @Override
    public LearningStrategyVO info(Long id, Long userId) {
        LearningStrategy strategy = learningStrategyDao.getById(id);
        Assert.isTrue(strategy != null, "The strategy id :" + id + " is not found");
        LearningStrategyVO vo = LearningStrategyMapper.INSTANCE.toVO(strategy);
        vo.setIntervals(this.getIntervalsByStrategyId(id));
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        learningStrategyDao.deleteById(id);
        return true;
    }
    
    @Override
    public Page<LearningStrategyVO> page(LearningStrategyQO qo) {
        Page<LearningStrategyVO> pageRes = new Page<>();
        Page<LearningStrategy> pages = learningStrategyDao.queryPage(
                new Page<>(qo.getPageNo(), qo.getPageSize()), 
                new ZCondition<LearningStrategy>()
                        .eq(qo.getUserId() != null, LearningStrategy::getUserId, qo.getUserId())
                        .like(qo.getName() != null, LearningStrategy::getName, qo.getName()));
        
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(LearningStrategyMapper.INSTANCE.toVOs(pages.getDataList()));

        
        return pageRes;
    }
    
    @Override
    public List<LearningStrategyVO> list(LearningStrategyQO qo) {
        List<LearningStrategy> strategies = learningStrategyDao.queryList(
                new ZCondition<LearningStrategy>()
                        .eq(qo.getUserId() != null, LearningStrategy::getUserId, qo.getUserId())
                        .like(qo.getName() != null, LearningStrategy::getName, qo.getName()));
        return LearningStrategyMapper.INSTANCE.toVOs(strategies);
    }

    private List<LearningStrategyIntervalVO> getIntervalsByStrategyId(Long strategyId) {
        List<LearningStrategyInterval> intervals = learningStrategyIntervalDao.queryList(
                new ZCondition<LearningStrategyInterval>()
                        .eq(LearningStrategyInterval::getStrategyId, strategyId)
                        .order(LearningStrategyInterval::getSequence, ZOrder.ASC));
        if(CollUtil.isEmpty(intervals)){
            return Collections.emptyList();
        }
        return LearningStrategyIntervalMapper.INSTANCE.toVOs(intervals);
    }

    @Override
    public LearnStrategyNextVO calculateNext(Long strategyId, Long currStrategyIntervalId) {
        // 获取当前策略的所有间隔设置
        List<LearningStrategyInterval> intervals = learningStrategyIntervalDao.queryList(
                new ZCondition<LearningStrategyInterval>()
                        .eq(LearningStrategyInterval::getStrategyId, strategyId)
                        .order(LearningStrategyInterval::getSequence, ZOrder.ASC));
        
        if (CollUtil.isEmpty(intervals)) {
            log.error("The strategy id :{} is no intervals", strategyId);
            throw new ServiceException("The strategy id :" + strategyId + " 's intervals not found");
        }
        LearnStrategyNextVO result = new LearnStrategyNextVO();

        // 根据序号排序
        Map<Integer, LearningStrategyInterval> sequenceMap = intervals.stream()
                .collect(Collectors.toMap(LearningStrategyInterval::getSequence, Function.identity()));
        // 如果没有当前间隔ID，则返回第一个间隔
        if (currStrategyIntervalId == null) {
            LearningStrategyInterval firstInterval = sequenceMap.get(0);
            if(firstInterval == null){
                log.error("The strategy id :{} is no sequence 0 interval", strategyId);
                throw new ServiceException("The strategy id :" + strategyId + " 's no sequence 0 interval");
            }
            result.setNextStrategyIntervalId(firstInterval.getId());
            result.setMasteryLevel(firstInterval.getRequiredMasteryLevel());
            result.setNextReviewTime(LocalDateTime.now().plusHours(firstInterval.getIntervalHours()));
            return result;
        }

        LearningStrategyInterval currInterval = intervals.stream().filter(interval ->
                interval.getId().equals(currStrategyIntervalId)).findFirst().orElse(null);
        if (currInterval == null) {
            log.error("The strategy id :{} is no interval id :{}", strategyId, currStrategyIntervalId);
            throw new ServiceException("The strategy id :" + strategyId + " 's interval id :" + currStrategyIntervalId + " not found");
        }

        int nextSequence = currInterval.getSequence() + 1;
        if(nextSequence == intervals.stream().map(LearningStrategyInterval::getSequence)
                .max(Comparator.comparingInt(Integer::intValue)).get()){
            result.setFinished(true);
            return result;
        }

        // 查找下一个间隔
        LearningStrategyInterval nextInterval = sequenceMap.get(currInterval.getSequence() + 1);
        if(nextInterval == null){
            log.error("The strategy id :{} is no sequence {} interval", strategyId, nextSequence);
            throw new ServiceException("The strategy id :" + strategyId + " 's no sequence " + nextSequence + " interval");
        }

        result.setNextStrategyIntervalId(nextInterval.getId());
        result.setMasteryLevel(nextInterval.getRequiredMasteryLevel());
        result.setNextReviewTime(LocalDateTime.now().plusHours(nextInterval.getIntervalHours()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultStrategy(Long strategyId, Long userId) {
        // 将该用户的所有策略设置为非默认
        ZUpdateCondition<LearningStrategy> unDefault = new ZUpdateCondition<>();
        unDefault.eq(LearningStrategy::getUserId,userId);
        unDefault.set(LearningStrategy::getIsDefault,false);
        learningStrategyDao.update(unDefault);
        
        // 将指定策略设置为默认
        ZUpdateCondition<LearningStrategy> setDefault = new ZUpdateCondition<>();
        setDefault.set(LearningStrategy::getIsDefault,true);
        setDefault.eq(BaseEntity::getId,strategyId);
        learningStrategyDao.update(setDefault);
        return true;
    }
}
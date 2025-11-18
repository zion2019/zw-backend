package com.zion.learning.strategy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.vo.learning.request.LearningStrategyIntervalQO;
import com.zion.common.vo.learning.request.LearningStrategyQO;
import com.zion.common.vo.learning.response.LearnStrategyNextVO;
import com.zion.common.vo.learning.response.LearningStrategyIntervalVO;
import com.zion.common.vo.learning.response.LearningStrategyVO;
import com.zion.learning.strategy.mapper.LearningStrategyIntervalMapper;
import com.zion.learning.strategy.mapper.LearningStrategyMapper;
import com.zion.learning.strategy.service.LearningStrategyService;
import com.zion.learning.strategy.model.LearningStrategy;
import com.zion.learning.strategy.dao.LearningStrategyDao;
import com.zion.learning.strategy.model.LearningStrategyInterval;
import com.zion.learning.strategy.dao.LearningStrategyIntervalDao;
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

        // save intervals
        this.saveIntervals(entity.getId(),qo.getIntervals());
        return true;
    }
    private void saveIntervals(Long strategyId, List<LearningStrategyIntervalQO> intervals) {
        List<LearningStrategyInterval> newIntervals = new ArrayList<>(intervals.size());
        intervals.forEach(i -> newIntervals.add(LearningStrategyIntervalMapper.INSTANCE.toEntity( i)));

        // 先删除原有的间隔设置
        LearningStrategyInterval deleteCondition = LearningStrategyInterval.builder().strategyId(strategyId).build();
        learningStrategyIntervalDao.delete(deleteCondition);

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
        LearningStrategy condition = LearningStrategy.builder().build();
        condition.setId(id);
        condition.setUserId(userId);
        LearningStrategy strategy = learningStrategyDao.conditionOne(condition);
        Assert.isTrue(strategy != null,"The strategy id :"+id+" is not found");
        LearningStrategyVO vo = LearningStrategyMapper.INSTANCE.toVO(strategy);
        vo.setIntervals(this.getIntervalsByStrategyId(id));
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        LearningStrategy condition = LearningStrategy.builder().build();
        condition.setId(id);
        learningStrategyDao.delete(condition);
        return true;
    }
    
    @Override
    public Page<LearningStrategyVO> page(LearningStrategyQO qo) {
        Page<LearningStrategyVO> pageRes = new Page<>();
        Page<LearningStrategy> pages = learningStrategyDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), LearningStrategy.class, buildConditionFromQO(qo));
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(LearningStrategyMapper.INSTANCE.toVOs(pages.getDataList()));
        
        // 批量查询并填充intervals
        List<LearningStrategyVO> dataList = pageRes.getDataList();
        if (CollUtil.isNotEmpty(dataList)) {
            List<Long> strategyIds = dataList.stream()
                    .map(LearningStrategyVO::getId)
                    .toList();
            
            // 批量获取所有策略的间隔设置
            LearningStrategyInterval intervalCondition = LearningStrategyInterval.builder().build();
            List<LearningStrategyInterval> allIntervals = learningStrategyIntervalDao.condition(intervalCondition);
            
            // 按策略ID分组
            Map<Long, List<LearningStrategyInterval>> intervalsGroupedByStrategy = new HashMap<>();
            if (CollUtil.isNotEmpty(allIntervals)) {
                intervalsGroupedByStrategy = allIntervals.stream()
                        .filter(interval -> strategyIds.contains(interval.getStrategyId()))
                        .collect(Collectors.groupingBy(LearningStrategyInterval::getStrategyId));
            }
            
            // 填充每个策略的间隔设置
            for (LearningStrategyVO strategy : dataList) {
                List<LearningStrategyInterval> intervals = intervalsGroupedByStrategy.get(strategy.getId());
                if (CollUtil.isNotEmpty(intervals)) {
                    strategy.setIntervals(LearningStrategyIntervalMapper.INSTANCE.toVOs(intervals));
                }
            }
        }
        
        return pageRes;
    }
    
    @Override
    public List<LearningStrategyVO> list(LearningStrategyQO qo) {
        List<LearningStrategy> strategies = learningStrategyDao.condition(buildConditionFromQO(qo));
        return LearningStrategyMapper.INSTANCE.toVOs(strategies);
    }



    private List<LearningStrategyIntervalVO> getIntervalsByStrategyId(Long strategyId) {
        LearningStrategyInterval condition = LearningStrategyInterval.builder().strategyId(strategyId).build();
        List<LearningStrategyInterval> intervals = learningStrategyIntervalDao.condition(condition);
        Assert.isTrue(CollUtil.isNotEmpty( intervals),"The strategy id "+strategyId+" 's intervals not found");
        return LearningStrategyIntervalMapper.INSTANCE.toVOs(intervals);
    }


    @Override
    public LearnStrategyNextVO calculateNext(Long strategyId, Long currStrategyIntervalId) {
        // 获取当前策略的所有间隔设置
        LearningStrategyInterval condition = LearningStrategyInterval.builder().strategyId(strategyId).build();
        List<LearningStrategyInterval> intervals = learningStrategyIntervalDao.condition(condition);
        if (CollUtil.isEmpty(intervals)) {
            log.error("The strategy id :{} is no intervals", strategyId);
            throw new ServiceException("The strategy id :"+strategyId+" 's intervals not found");
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
                throw new ServiceException("The strategy id :"+strategyId+" 's no sequence 0 interval");
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
            throw new ServiceException("The strategy id :"+strategyId+" 's interval id :"+currStrategyIntervalId+" not found");
        }

        int nextSequence = currInterval.getSequence() + 1;
        if(nextSequence == intervals.stream().map(LearningStrategyInterval::getSequence)
                .max(Comparator.comparingInt(Integer::intValue)).get()){
            result.setFinished( true);
            return result;
        }

        // 查找下一个间隔
        LearningStrategyInterval nextInterval = sequenceMap.get(currInterval.getSequence()+1);
        if(nextInterval == null){
            log.error("The strategy id :{} is no sequence {} interval", strategyId, nextSequence);
            throw new ServiceException("The strategy id :"+strategyId+" 's no sequence "+nextSequence+" interval");
        }

        result.setNextStrategyIntervalId(nextInterval.getId());
        result.setMasteryLevel(nextInterval.getRequiredMasteryLevel());
        result.setNextReviewTime(LocalDateTime.now().plusHours(nextInterval.getIntervalHours()));
        return result;

    }
    
    /**
     * 构建查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private LearningStrategy buildConditionFromQO(LearningStrategyQO qo) {
        LearningStrategy condition = LearningStrategy.builder().build();
        condition.setId(qo.getId());
        condition.setName(qo.getName());
        condition.setDescription(qo.getDescription());
        condition.setAllowFallback(qo.getAllowFallback());
        condition.setIsSystemDefault(qo.getIsSystemDefault());
        condition.setUserId(qo.getUserId());
        return condition;
    }
}
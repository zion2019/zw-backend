package com.zion.learning.strategy.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.LearningStrategyQO;
import com.zion.common.vo.learning.response.LearnStrategyNextVO;
import com.zion.common.vo.learning.response.LearningStrategyVO;

import java.util.List;

public interface LearningStrategyService {
    
    /**
     * 创建或更新学习策略
     * @param qo 学习策略对象
     * @return 是否成功
     */
    boolean save(LearningStrategyQO qo);
    
    /**
     * 获取学习策略详情
     * @param id 策略ID
     * @param userId 用户ID
     * @return 学习策略详情
     */
    LearningStrategyVO info(Long id, Long userId);
    
    /**
     * 删除学习策略
     * @param id 策略ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 分页查询学习策略列表
     * @param qo 查询条件
     * @return 学习策略分页列表
     */
    Page<LearningStrategyVO> page(LearningStrategyQO qo);
    
    /**
     * 根据条件查询学习策略列表
     * @param qo 查询条件
     * @return 学习策略列表
     */
    List<LearningStrategyVO> list(LearningStrategyQO qo);

    /**
     * 计算下次复习信息
     *
     * @param strategyId 学习策略ID
     * @param currStrategyIntervalId 当前策略的间隔ID,为空则首次复习
     * @return 下次复习信息
     */
    LearnStrategyNextVO calculateNext(Long strategyId, Long currStrategyIntervalId);
}
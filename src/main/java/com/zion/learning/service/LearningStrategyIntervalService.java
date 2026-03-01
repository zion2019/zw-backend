package com.zion.learning.service;

import com.zion.common.vo.learning.request.LearningStrategyIntervalQO;
import com.zion.common.vo.learning.response.LearningStrategyIntervalVO;

public interface LearningStrategyIntervalService {
    
    /**
     * 创建或更新间隔配置
     * @param qo 间隔配置对象
     * @return 是否成功
     */
    boolean save(LearningStrategyIntervalQO qo);
    
    /**
     * 获取间隔配置详情
     * @param id 间隔配置ID
     * @return 间隔配置详情
     */
    LearningStrategyIntervalVO info(Long id);
    
    /**
     * 删除间隔配置
     * @param id 间隔配置ID
     * @return 是否成功
     */
    boolean delete(Long id);
}
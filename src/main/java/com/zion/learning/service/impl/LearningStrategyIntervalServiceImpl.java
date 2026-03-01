package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.ServiceException;
import com.zion.common.db.ZCondition;
import com.zion.common.db.ZOrder;
import com.zion.common.vo.learning.request.LearningStrategyIntervalQO;
import com.zion.common.vo.learning.response.LearningStrategyIntervalVO;
import com.zion.learning.dao.LearningStrategyIntervalDao;
import com.zion.learning.mapper.LearningStrategyIntervalMapper;
import com.zion.learning.model.LearningStrategyInterval;
import com.zion.learning.service.LearningStrategyIntervalService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class LearningStrategyIntervalServiceImpl implements LearningStrategyIntervalService {
    
    @Resource
    private LearningStrategyIntervalDao learningStrategyIntervalDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(LearningStrategyIntervalQO qo) {
        LearningStrategyInterval entity = LearningStrategyIntervalMapper.INSTANCE.toEntity(qo);
        learningStrategyIntervalDao.save(entity);
        return true;
    }
    
    @Override
    public LearningStrategyIntervalVO info(Long id) {
        LearningStrategyInterval interval = learningStrategyIntervalDao.getById(id);
        if (interval == null) {
            return null;
        }
        return LearningStrategyIntervalMapper.INSTANCE.toVO(interval);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        LearningStrategyInterval interval = learningStrategyIntervalDao.getById(id);
        if (interval == null) {
            log.error("间隔配置不存在:{}", id);
            throw new ServiceException("间隔配置不存在");
        }
        
        learningStrategyIntervalDao.deleteById(id);
        return true;
    }
}
package com.zion.learning.strategy.mapper;

import com.zion.learning.strategy.model.LearningStrategyInterval;
import com.zion.common.vo.learning.response.LearningStrategyIntervalVO;
import com.zion.common.vo.learning.request.LearningStrategyIntervalQO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LearningStrategyIntervalMapper {
    
    LearningStrategyIntervalMapper INSTANCE = Mappers.getMapper(LearningStrategyIntervalMapper.class);
    
    LearningStrategyIntervalVO toVO(LearningStrategyInterval interval);
    
    LearningStrategyInterval toEntity(LearningStrategyIntervalQO qo);
    
    List<LearningStrategyIntervalVO> toVOs(List<LearningStrategyInterval> intervals);
}
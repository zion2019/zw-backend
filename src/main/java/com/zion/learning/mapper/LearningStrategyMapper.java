package com.zion.learning.mapper;

import com.zion.learning.model.LearningStrategy;
import com.zion.common.vo.learning.response.LearningStrategyVO;
import com.zion.common.vo.learning.request.LearningStrategyQO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LearningStrategyMapper {
    
    LearningStrategyMapper INSTANCE = Mappers.getMapper(LearningStrategyMapper.class);
    
    LearningStrategyVO toVO(LearningStrategy strategy);
    
    LearningStrategy toEntity(LearningStrategyQO qo);
    
    List<LearningStrategyVO> toVOs(List<LearningStrategy> strategies);
}
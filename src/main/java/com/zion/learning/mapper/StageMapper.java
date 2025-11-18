package com.zion.learning.mapper;

import com.zion.learning.model.Stage;
import com.zion.common.vo.learning.response.StageVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StageMapper {
    
    StageMapper INSTANCE = Mappers.getMapper(StageMapper.class);
    
    StageVO toVO(Stage stage);
    
    Stage toEntity(StageVO stageVO);
    
    List<StageVO> toVOs(List<Stage> stages);
}
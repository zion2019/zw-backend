package com.zion.learning.mapper;

import com.zion.learning.model.KnowledgePoint;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import com.zion.learning.common.constents.MasteryLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface KnowledgePointMapper {
    
    KnowledgePointMapper INSTANCE = Mappers.getMapper(KnowledgePointMapper.class);
    
    @Mapping(source = "masteryLevelCode", target = "masteryLevel", qualifiedByName = "codeToMasteryLevel")
    KnowledgePointVO toVO(KnowledgePoint knowledgePoint);
    
    @Mapping(source = "masteryLevel", target = "masteryLevelCode", qualifiedByName = "masteryLevelToCode")
    KnowledgePoint toEntity(KnowledgePointVO knowledgePointVO);
    
    List<KnowledgePointVO> toVOs(List<KnowledgePoint> knowledgePoints);
    
    @Named("codeToMasteryLevel")
    default MasteryLevel codeToMasteryLevel(Integer code) {
        return MasteryLevel.fromCode(code);
    }
    
    @Named("masteryLevelToCode")
    default Integer masteryLevelToCode(MasteryLevel masteryLevel) {
        return masteryLevel != null ? masteryLevel.getCode() : null;
    }
}
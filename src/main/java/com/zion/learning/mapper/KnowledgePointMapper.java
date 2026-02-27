package com.zion.learning.mapper;

import com.zion.common.vo.learning.request.KnowledgePointQO;
import com.zion.learning.model.KnowledgePoint;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface KnowledgePointMapper {

    KnowledgePointMapper INSTANCE = Mappers.getMapper(KnowledgePointMapper.class);

    KnowledgePointVO toVO(KnowledgePoint knowledgePoint);

    KnowledgePoint toEntity(KnowledgePointQO knowledgePointVO);

    List<KnowledgePointVO> toVOs(List<KnowledgePoint> knowledgePoints);
}
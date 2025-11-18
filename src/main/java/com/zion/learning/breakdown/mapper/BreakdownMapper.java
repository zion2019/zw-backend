package com.zion.learning.breakdown.mapper;

import com.zion.learning.breakdown.model.Breakdown;
import com.zion.common.vo.learning.response.BreakdownVO;
import com.zion.common.vo.learning.request.BreakdownQO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BreakdownMapper {
    
    BreakdownMapper INSTANCE = Mappers.getMapper(BreakdownMapper.class);
    
    BreakdownVO toVO(Breakdown breakdown);
    
    Breakdown toEntity(BreakdownQO qo);
    
    List<BreakdownVO> toVOs(List<Breakdown> breakdowns);
}
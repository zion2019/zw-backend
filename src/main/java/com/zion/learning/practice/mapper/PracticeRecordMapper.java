package com.zion.learning.practice.mapper;

import com.zion.learning.practice.model.PracticeRecord;
import com.zion.common.vo.learning.response.PracticeRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PracticeRecordMapper {
    
    PracticeRecordMapper INSTANCE = Mappers.getMapper(PracticeRecordMapper.class);
    
    PracticeRecordVO toVO(PracticeRecord practiceRecord);
    
    PracticeRecord toEntity(PracticeRecordVO practiceRecordVO);
    
    List<PracticeRecordVO> toVOs(List<PracticeRecord> practiceRecords);
}
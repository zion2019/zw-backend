package com.zion.learning.mapper;

import com.zion.common.vo.learning.request.PracticeRecordQO;
import com.zion.learning.model.PracticeRecord;
import com.zion.common.vo.learning.response.PracticeRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PracticeRecordMapper {
    
    PracticeRecordMapper INSTANCE = Mappers.getMapper(PracticeRecordMapper.class);
    
    PracticeRecordVO toVO(PracticeRecord practiceRecord);
    
    PracticeRecord toEntity(PracticeRecordQO qo);
    
    List<PracticeRecordVO> toVOs(List<PracticeRecord> practiceRecords);
}
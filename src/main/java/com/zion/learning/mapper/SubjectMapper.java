package com.zion.learning.mapper;

import com.zion.learning.model.Subject;
import com.zion.common.vo.learning.response.SubjectVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SubjectMapper {
    
    SubjectMapper INSTANCE = Mappers.getMapper(SubjectMapper.class);
    
    SubjectVO toVO(Subject subject);
    
    Subject toEntity(SubjectVO subjectVO);
    
    List<SubjectVO> toVOs(List<Subject> subjects);
}
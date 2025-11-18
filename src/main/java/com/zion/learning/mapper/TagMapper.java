package com.zion.learning.mapper;

import com.zion.learning.model.Tag;
import com.zion.common.vo.learning.response.TagVO;
import com.zion.common.vo.learning.request.TagQO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TagMapper {
    
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);
    
    TagVO toVO(Tag tag);
    
    Tag toEntity(TagQO qo);
    
    List<TagVO> toVOs(List<Tag> tags);
}
package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.common.db.ZCondition;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.learning.request.TagQO;
import com.zion.common.vo.learning.response.TagVO;
import com.zion.learning.mapper.TagMapper;
import com.zion.learning.service.TagService;
import com.zion.learning.model.Tag;
import com.zion.learning.dao.TagDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TagServiceImpl implements TagService {
    
    @Resource
    private TagDao tagDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(TagQO qo) {
        Tag entity = TagMapper.INSTANCE.toEntity(qo);
        BaseEntityUtil.filedBasicInfo(entity);
        tagDao.save(entity);
        return true;
    }
    
    @Override
    public TagVO info(Long id, Long userId) {
        Tag tag = tagDao.getById(id);
        if(tag == null){
            return null;
        }
        return TagMapper.INSTANCE.toVO(tag);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        tagDao.deleteById(id);
        return true;
    }
    
    @Override
    public Page<TagVO> page(TagQO qo) {
        Page<TagVO> pageRes = new Page<>();
        Page<Tag> pages = tagDao.queryPage(
                new Page<>(qo.getPageNo(), qo.getPageSize()), 
                new ZCondition<Tag>()
                        .eq(qo.getUserId() != null, Tag::getUserId, qo.getUserId())
                        .eq(qo.getParentId() != null, Tag::getParentId, qo.getParentId())
                        .like(qo.getName() != null, Tag::getName, qo.getName()));
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(TagMapper.INSTANCE.toVOs(pages.getDataList()));
        return pageRes;
    }
    
    @Override
    public List<TagVO> list(TagQO qo) {
        List<Tag> tags = tagDao.queryList(
                new ZCondition<Tag>()
                        .eq(qo.getUserId() != null, Tag::getUserId, qo.getUserId())
                        .eq(qo.getParentId() != null, Tag::getParentId, qo.getParentId())
                        .like(qo.getName() != null, Tag::getName, qo.getName()));
        return TagMapper.INSTANCE.toVOs(tags);
    }
    
    @Override
    public List<TagVO> tree(Long userId) {
        List<Tag> tags = tagDao.queryList(
                new ZCondition<Tag>()
                        .eq(Tag::getUserId, userId));
        return TagMapper.INSTANCE.toVOs(tags);
    }
    
    /**
     * 构建查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private Tag buildConditionFromQO(TagQO qo) {
        Tag condition = Tag.builder().build();
        condition.setId(qo.getId());
        condition.setName(qo.getName());
        condition.setColor(qo.getColor());
        condition.setIcon(qo.getIcon());
        condition.setParentId(qo.getParentId());
        condition.setSortOrder(qo.getSortOrder());
        condition.setUserId(qo.getUserId());
        return condition;
    }
}
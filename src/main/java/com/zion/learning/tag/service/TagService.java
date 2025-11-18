package com.zion.learning.tag.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.TagQO;
import com.zion.common.vo.learning.response.TagVO;

import java.util.List;

public interface TagService {
    
    /**
     * 创建或更新标签
     * @param qo 标签对象
     * @return 是否成功
     */
    boolean save(TagQO qo);
    
    /**
     * 获取标签详情
     * @param id 标签ID
     * @param userId 用户ID
     * @return 标签详情
     */
    TagVO info(Long id, Long userId);
    
    /**
     * 删除标签
     * @param id 标签ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 分页查询标签列表
     * @param qo 查询条件
     * @return 标签分页列表
     */
    Page<TagVO> page(TagQO qo);
    
    /**
     * 根据条件查询标签列表
     * @param qo 查询条件
     * @return 标签列表
     */
    List<TagVO> list(TagQO qo);
    
    /**
     * 获取标签树结构
     * @param userId 用户ID
     * @return 标签树
     */
    List<TagVO> tree(Long userId);
}
package com.zion.learning.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.SubjectQO;
import com.zion.common.vo.learning.response.SubjectVO;

import java.util.List;

public interface SubjectService {
    
    /**
     * 创建或更新科目
     * @param subject 科目对象
     * @return 是否成功
     */
    boolean save(SubjectQO subject);
    
    /**
     * 获取科目详情
     * @param id 科目ID
     * @param userId 用户ID
     * @return 科目详情
     */
    SubjectVO info(Long id, Long userId);
    
    /**
     * 删除科目
     * @param id 科目ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 分页查询科目列表
     * @param qo 查询条件
     * @return 科目分页列表
     */
    Page<SubjectVO> page(SubjectQO qo);
    
    /**
     * 刷新指定科目的统计信息
     * @param qo 刷新信息
     * @return 是否成功
     */
    boolean refreshStats(SubjectQO qo);
    
    /**
     * 根据条件查询科目列表
     * @param qo 查询条件
     * @return 科目列表
     */
    List<SubjectVO> list(SubjectQO qo);

    /**
     * 获取今日复习科目列表（首页用）
     * @param currentUserId 当前用户ID
     * @return 今日复习列表
     */
    SubjectVO getTodayReviewList(Long currentUserId);
}
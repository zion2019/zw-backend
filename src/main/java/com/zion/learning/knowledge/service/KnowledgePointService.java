package com.zion.learning.knowledge.service;

import com.zion.common.basic.Page;
import com.zion.learning.common.constents.PracticeResult;
import com.zion.learning.knowledge.model.KnowledgePoint;
import com.zion.common.vo.learning.request.KnowledgePointQO;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import com.zion.common.vo.learning.response.KnowledgePointPracticeVO;

import java.util.List;

public interface KnowledgePointService {
    
    /**
     * 创建或更新知识点
     * @param qo 知识点查询对象
     * @return 是否成功
     */
    boolean save(KnowledgePointQO qo);
    
    /**
     * 获取知识点详情
     * @param id 知识点ID
     * @param userId 用户ID
     * @return 知识点详情
     */
    KnowledgePointVO info(Long id, Long userId);
    
    /**
     * 删除知识点
     * @param id 知识点ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 分页查询知识点列表
     * @param qo 查询条件
     * @return 知识点分页列表
     */
    Page<KnowledgePointVO> page(KnowledgePointQO qo);
    
    /**
     * 根据条件查询知识点列表
     * @param qo 查询条件
     * @return 知识点列表
     */
    List<KnowledgePointVO> list(KnowledgePointQO qo);
    
    /**
     * 知识点练习
     * @param knowledgePointId 知识点ID
     * @param result 练习结果
     * @param userId 用户ID
     */
    void practice(Long knowledgePointId, PracticeResult result, Long userId);

    /**
     * 更新知识点的 breakdownCnt
     * @param knowledgePointId 知识点ID
     * @param breakDownCnt breakdownCnt
     */
    void updateBreakDownCnt(Long knowledgePointId, Integer breakDownCnt);

    /**
     * 获取需要复习的 subjectIds
     * @param currentUserId 当前用户ID
     * @param count 需要获取的 subjectId 个数
     * @return subjectIds
     */
    List<Long> getReviewTopSubjectIds(Long currentUserId, int count);
}
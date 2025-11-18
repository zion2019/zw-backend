package com.zion.learning.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.BreakdownQO;
import com.zion.common.vo.learning.response.BreakdownVO;

import java.util.List;

public interface BreakdownService {
    
    /**
     * 创建或更新拆解点
     * @param qo 拆解点对象
     * @return 是否成功
     */
    boolean save(BreakdownQO qo);
    
    /**
     * 获取拆解点详情
     * @param id 拆解点ID
     * @param userId 用户ID
     * @return 拆解点详情
     */
    BreakdownVO info(Long id, Long userId);
    
    /**
     * 删除拆解点
     * @param id 拆解点ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 分页查询拆解点列表
     * @param qo 查询条件
     * @return 拆解点分页列表
     */
    Page<BreakdownVO> page(BreakdownQO qo);
    
    /**
     * 根据条件查询拆解点列表
     * @param qo 查询条件
     * @return 拆解点列表
     */
    List<BreakdownVO> list(BreakdownQO qo);
}
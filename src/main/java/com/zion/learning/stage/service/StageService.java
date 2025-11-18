package com.zion.learning.stage.service;

import com.zion.common.basic.Page;
import com.zion.learning.stage.model.Stage;
import com.zion.common.vo.learning.request.StageQO;
import com.zion.common.vo.learning.response.StageVO;

import java.util.List;

public interface StageService {
    
    /**
     * 创建或更新阶段
     * @param qo 阶段查询对象
     * @return 是否成功
     */
    boolean save(StageQO qo);
    
    /**
     * 获取阶段详情
     * @param id 阶段ID
     * @param userId 用户ID
     * @return 阶段详情
     */
    StageVO info(Long id, Long userId);
    
    /**
     * 删除阶段
     * @param id 阶段ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 分页查询阶段列表
     * @param qo 查询条件
     * @return 阶段分页列表
     */
    Page<StageVO> page(StageQO qo);
    
    /**
     * 根据条件查询阶段列表
     * @param condition 查询条件
     * @return 阶段列表
     */
    List<StageVO> list(Stage condition);
    
    /**
     * 刷新指定阶段的知识点计数
     * @param qo 刷新信息
     * @return 是否成功
     */
    boolean refreshStats(StageQO qo);
}
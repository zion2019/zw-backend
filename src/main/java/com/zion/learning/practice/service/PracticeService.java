package com.zion.learning.practice.service;

import com.zion.common.basic.Page;
import com.zion.learning.practice.model.PracticeRecord;
import com.zion.common.vo.learning.request.PracticeRecordQO;
import com.zion.common.vo.learning.response.PracticeRecordVO;

import java.util.List;

public interface PracticeService {
    
    /**
     * 创建或更新练习记录
     * @param qo 练习记录查询对象
     * @return 是否成功
     */
    boolean practice(PracticeRecordQO qo);
    
    /**
     * 获取练习记录详情
     * @param id 记录ID
     * @param userId 用户ID
     * @return 练习记录详情
     */
    PracticeRecordVO info(Long id, Long userId);
    
    /**
     * 删除练习记录
     * @param id 记录ID
     * @return 是否成功
     */
    boolean delete(Long id);
    
    /**
     * 分页查询练习记录列表
     * @param qo 查询条件
     * @return 练习记录分页列表
     */
    Page<PracticeRecordVO> page(PracticeRecordQO qo);
    
    /**
     * 根据条件查询练习记录列表
     * @param condition 查询条件
     * @return 练习记录列表
     */
    List<PracticeRecordVO> list(PracticeRecord condition);


}
package com.zion.learning.dao;

import com.zion.common.db.ZDao;
import com.zion.learning.model.Subject;

import java.util.List;

public interface SubjectDao extends ZDao<Subject> {
    
    /**
     * 获取最近使用频率最高的tagId列表
     * @param num 需要获取的数量
     * @param userId 用户ID
     * @return tagId列表
     */
    List<Long> getRecentlyTagIds(int num, Long userId);
}
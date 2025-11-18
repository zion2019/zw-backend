package com.zion.learning.service;

import com.zion.learning.common.constents.MasteryLevel;
import com.zion.learning.common.constents.PracticeResult;
import org.springframework.stereotype.Service;

/**
 * 艾宾浩斯遗忘曲线服务
 */
@Service
public class EbbinghausService {
    
    /**
     * 根据练习结果更新掌握程度
     * 
     * @param result 练习结果
     * @param currentLevel 当前掌握程度
     * @return 更新后的掌握程度
     */
    public MasteryLevel updateMasteryLevel(PracticeResult result, MasteryLevel currentLevel) {
        return MasteryLevel.updateMasteryLevel(result, currentLevel);
    }
}
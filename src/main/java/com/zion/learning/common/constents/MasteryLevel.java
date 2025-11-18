package com.zion.learning.common.constents;

import lombok.Getter;

/**
 * 掌握程度枚举
 */
@Getter
public enum MasteryLevel {
    STRANGER(0, "陌生"),      // 初始状态
    FAMILIAR(1, "熟悉"),      // 第一阶段
    UNDERSTAND(2, "掌握"),    // 第二阶段
    MASTER(3, "精通");        // 最终阶段

    private final Integer code;
    private final String description;

    MasteryLevel(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MasteryLevel fromCode(Integer code) {
        if (code == null) {
            return STRANGER;
        }
        for (MasteryLevel level : MasteryLevel.values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        return STRANGER;
    }

    /**
     * 根据练习结果更新掌握程度
     * @param result 练习结果
     * @param currentLevel 当前掌握程度
     * @return 更新后的掌握程度
     */
    public static MasteryLevel updateMasteryLevel(PracticeResult result, MasteryLevel currentLevel) {
        if (currentLevel == null) {
            currentLevel = STRANGER;
        }

        switch (result) {
            case REMEMBER:
                // 选择【记得】进入下一阶段
                if (currentLevel == MASTER) {
                    return MASTER;
                }
                return fromCode(currentLevel.code + 1);
                
            case BLUR:
                // 选择【模糊】退回上一阶段
                if (currentLevel == STRANGER) {
                    return STRANGER;
                }
                return fromCode(currentLevel.code - 1);
                
            case FORGET:
                // 选择【忘记】回到第一阶段
                return STRANGER;
                
            default:
                return currentLevel;
        }
    }
}
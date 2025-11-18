package com.zion.learning.common.constents;

import lombok.Getter;

/**
 * 练习结果枚举
 */
@Getter
public enum PracticeResult {
    REMEMBER(1, "记得"),      // 进入下一阶段
    BLUR(-1, "模糊"),         // 退回上一阶段或保持
    FORGET(-2, "忘记");       // 回到第一阶段

    private final Integer code;
    private final String description;

    PracticeResult(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PracticeResult fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PracticeResult result : PracticeResult.values()) {
            if (result.code.equals(code)) {
                return result;
            }
        }
        return null;
    }
}
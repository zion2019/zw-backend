package com.zion.common.vo.learning.response;

import com.zion.learning.common.constents.MasteryLevel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LearnStrategyNextVO {

    /**
     * 是否完结
     */
    private boolean isFinished;

    /**
     * 下次复习时间
     */
    private LocalDateTime nextReviewTime;


    /**
     * 下次复习的策略ID
     */
    private Long nextStrategyIntervalId;

    /**
     * 掌握程度
     */
    private MasteryLevel masteryLevel;
}

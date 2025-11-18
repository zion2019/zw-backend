package com.zion.learning.service;

import com.zion.common.vo.learning.response.StatisticsSummaryVO;
import com.zion.common.vo.learning.response.MasteryBubbleVO;
import com.zion.common.vo.learning.response.ChartDataVO;
import com.zion.common.vo.learning.response.KeyIndicatorVO;

import java.util.List;

public interface StatisticsService {
    
    /**
     * 获取统计总数
     * @param userId 用户ID
     * @return 统计总数VO
     */
    StatisticsSummaryVO getSummary(Long userId);
    
    /**
     * 获取掌握度气泡数据
     * @param userId 用户ID
     * @return 掌握度气泡列表
     */
    List<MasteryBubbleVO> getMasteryBubbles(Long userId);
    
    /**
     * 获取折线图数据
     * @param userId 用户ID
     * @param days 天数范围
     * @return 折线图数据列表
     */
    List<ChartDataVO> getChartData(Long userId, Integer days);
    
    /**
     * 获取关键指标
     * @param userId 用户ID
     * @return 关键指标VO
     */
    KeyIndicatorVO getKeyIndicators(Long userId);
}
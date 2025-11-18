package com.zion.learning.statistics.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.response.StatisticsSummaryVO;
import com.zion.common.vo.learning.response.MasteryBubbleVO;
import com.zion.common.vo.learning.response.ChartDataVO;
import com.zion.common.vo.learning.response.KeyIndicatorVO;
import com.zion.learning.statistics.service.StatisticsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/learn/statistics")
public class StatisticsController extends BaseController {
    
    @Resource
    private StatisticsService statisticsService;
    
    /**
     * 获取统计总数
     */
    @GetMapping("/summary")
    public R<StatisticsSummaryVO> getSummary() {
        return R.ok(statisticsService.getSummary(getCurrentUserId()));
    }
    
    /**
     * 获取掌握度气泡
     */
    @GetMapping("/mastery-bubbles")
    public R<MasteryBubbleVO> getMasteryBubbles() {
        return R.ok(statisticsService.getMasteryBubbles(getCurrentUserId()));
    }
    
    /**
     * 获取折线图数据
     */
    @GetMapping("/chart-data")
    public R<ChartDataVO> getChartData(@RequestParam(defaultValue = "30") Integer days) {
        return R.ok(statisticsService.getChartData(getCurrentUserId(), days));
    }
    
    /**
     * 获取关键指标
     */
    @GetMapping("/key-indicators")
    public R<KeyIndicatorVO> getKeyIndicators() {
        return R.ok(statisticsService.getKeyIndicators(getCurrentUserId()));
    }
}
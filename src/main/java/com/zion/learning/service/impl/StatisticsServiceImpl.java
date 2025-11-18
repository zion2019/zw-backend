package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.db.ZCondition;
import com.zion.common.vo.learning.response.StatisticsSummaryVO;
import com.zion.common.vo.learning.response.MasteryBubbleVO;
import com.zion.common.vo.learning.response.ChartDataVO;
import com.zion.common.vo.learning.response.KeyIndicatorVO;
import com.zion.learning.service.StatisticsService;
import com.zion.learning.model.KnowledgePoint;
import com.zion.learning.dao.KnowledgePointDao;
import com.zion.learning.model.PracticeRecord;
import com.zion.learning.dao.PracticeRecordDao;
import com.zion.learning.common.constents.MasteryLevel;
import com.zion.learning.common.constents.PracticeResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Resource
    private KnowledgePointDao knowledgePointDao;
    
    @Resource
    private PracticeRecordDao practiceRecordDao;
    
    @Override
    public StatisticsSummaryVO getSummary(Long userId) {
        StatisticsSummaryVO summary = new StatisticsSummaryVO();
        
        // 获取用户所有知识点
        List<KnowledgePoint> knowledgePoints = knowledgePointDao.queryList(
                new ZCondition<KnowledgePoint>().eq(KnowledgePoint::getUserId, userId));
        
        // 知识点总数
        summary.setTotalKnowledgePoints(knowledgePoints.size());
        
        // 已精通知识点数
        long masteredCount = knowledgePoints.stream()
                .filter(kp -> MasteryLevel.MASTER.getCode().equals(kp.getMasteryLevelCode()))
                .count();
        summary.setMasteredKnowledgePoints((int) masteredCount);
        
        // 获取用户所有练习记录
        List<PracticeRecord> practiceRecords = practiceRecordDao.queryList(
                new ZCondition<PracticeRecord>().eq(PracticeRecord::getUserId, userId));
        
        // 复习总次数
        summary.setTotalReviewCount(practiceRecords.size());
        
        // 累计学习时长(这里简化处理，实际应该根据学习记录计算)
        // 假设每次复习平均花费10分钟
        summary.setTotalStudyHours(new BigDecimal(practiceRecords.size() * 10).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP));
        
        return summary;
    }
    
    @Override
    public List<MasteryBubbleVO> getMasteryBubbles(Long userId) {
        List<MasteryBubbleVO> bubbles = new ArrayList<>();
        
        // 获取最近30天内复习过的知识点
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        List<PracticeRecord> practiceRecords = practiceRecordDao.queryList(
                new ZCondition<PracticeRecord>()
                        .eq(PracticeRecord::getUserId, userId)
                        .ge(PracticeRecord::getPracticeStartTime, thirtyDaysAgo));
        
        // 过滤出最近30天的记录
        List<PracticeRecord> recentPracticeRecords = practiceRecords.stream()
                .filter(record -> record.getPracticeStartTime() != null)
                .collect(Collectors.toList());
        
        if (CollUtil.isEmpty(recentPracticeRecords)) {
            return bubbles;
        }
        
        // 获取涉及的知识点ID
        Set<Long> knowledgePointIds = recentPracticeRecords.stream()
                .map(PracticeRecord::getKnowledgePointId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        if (CollUtil.isEmpty(knowledgePointIds)) {
            return bubbles;
        }
        
        // 获取知识点信息
        List<KnowledgePoint> knowledgePoints = knowledgePointDao.queryList(
                new ZCondition<KnowledgePoint>()
                        .eq(KnowledgePoint::getUserId, userId)
                        .in(KnowledgePoint::getId, knowledgePointIds));
        
        Map<Long, KnowledgePoint> knowledgePointMap = knowledgePoints.stream()
                .collect(Collectors.toMap(KnowledgePoint::getId, kp -> kp));
        
        knowledgePointIds.forEach(knowledgePointId -> {
            KnowledgePoint knowledgePoint = knowledgePointMap.get(knowledgePointId);
            if (knowledgePoint != null) {
                MasteryBubbleVO bubble = new MasteryBubbleVO();
                bubble.setKnowledgePointId(knowledgePoint.getId());
                bubble.setKnowledgePointName(knowledgePoint.getTitle());
                bubble.setMasteryLevel(MasteryLevel.fromCode(knowledgePoint.getMasteryLevelCode()));
                
                // 获取该知识点的最后复习时间
                Optional<PracticeRecord> lastRecord = recentPracticeRecords.stream()
                        .filter(record -> knowledgePointId.equals(record.getKnowledgePointId()))
                        .max(Comparator.comparing(PracticeRecord::getPracticeStartTime));
                
                if (lastRecord.isPresent()) {
                    bubble.setLastReviewTime(lastRecord.get().getPracticeStartTime().toString());
                    bubbles.add(bubble);
                }
            }
        });
        
        return bubbles;
    }
    
    @Override
    public List<ChartDataVO> getChartData(Long userId, Integer days) {
        List<ChartDataVO> chartDataList = new ArrayList<>();
        
        // 计算起始日期
        LocalDateTime startDate = LocalDateTime.now().minusDays(days - 1); // 包含今天
        LocalDateTime endDate = LocalDateTime.now();
        
        // 获取用户练习记录
        List<PracticeRecord> practiceRecords = practiceRecordDao.queryList(
                new ZCondition<PracticeRecord>()
                        .eq(PracticeRecord::getUserId, userId)
                        .ge(PracticeRecord::getPracticeStartTime, startDate)
                        .le(PracticeRecord::getPracticeStartTime, endDate));
        
        // 按日期分组统计
        Map<LocalDate, List<PracticeRecord>> recordsByDate = practiceRecords.stream()
                .filter(record -> record.getPracticeStartTime() != null)
                .collect(Collectors.groupingBy(record -> record.getPracticeStartTime().toLocalDate()));
        
        // 生成完整的日期范围数据
        LocalDate currentDate = startDate.toLocalDate();
        while (!currentDate.isAfter(endDate.toLocalDate())) {
            ChartDataVO chartData = new ChartDataVO();
            chartData.setDate(java.sql.Date.valueOf(currentDate));
            
            List<PracticeRecord> dailyRecords = recordsByDate.getOrDefault(currentDate, Collections.emptyList());
            
            // 复习总量
            chartData.setTotalReviews(dailyRecords.size());
            
            // 记得量 (PracticeResult.REMEMBER 的 code 是 1)
            long rememberCount = dailyRecords.stream()
                    .filter(record -> record.getResult() != null && 
                            record.getResult() == PracticeResult.REMEMBER)
                    .count();
            chartData.setRememberCount((int) rememberCount);
            
            // 记得率
            if (dailyRecords.size() > 0) {
                BigDecimal rememberRate = new BigDecimal(rememberCount)
                        .multiply(new BigDecimal(100))
                        .divide(new BigDecimal(dailyRecords.size()), 2, RoundingMode.HALF_UP);
                chartData.setRememberRate(rememberRate);
            } else {
                chartData.setRememberRate(BigDecimal.ZERO);
            }
            
            chartDataList.add(chartData);
            currentDate = currentDate.plusDays(1);
        }
        
        // 按日期排序
        chartDataList.sort(Comparator.comparing(ChartDataVO::getDate));
        
        return chartDataList;
    }
    
    @Override
    public KeyIndicatorVO getKeyIndicators(Long userId) {
        KeyIndicatorVO keyIndicator = new KeyIndicatorVO();
        
        // 计算本月第一天和最后一天
        LocalDateTime firstDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime lastDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        
        // 计算最近30天的开始时间
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(29).withHour(0).withMinute(0).withSecond(0); // 30天包括今天
        
        // 获取用户练习记录
        List<PracticeRecord> practiceRecords = practiceRecordDao.queryList(
                new ZCondition<PracticeRecord>()
                        .eq(PracticeRecord::getUserId, userId));
        
        // 本月记录
        List<PracticeRecord> currentMonthRecords = practiceRecords.stream()
                .filter(record -> record.getPracticeStartTime() != null && 
                        !record.getPracticeStartTime().isBefore(firstDayOfMonth) && 
                        !record.getPracticeStartTime().isAfter(lastDayOfMonth))
                .collect(Collectors.toList());
        
        // 最近30天记录
        List<PracticeRecord> recent30DaysRecords = practiceRecords.stream()
                .filter(record -> record.getPracticeStartTime() != null && 
                        !record.getPracticeStartTime().isBefore(thirtyDaysAgo) && 
                        !record.getPracticeStartTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        
        // 本月统计数据
        keyIndicator.setCurrentMonthTotalReviews(currentMonthRecords.size());
        long currentMonthRememberCount = currentMonthRecords.stream()
                .filter(record -> record.getResult() != null && record.getResult() == PracticeResult.REMEMBER)
                .count();
        keyIndicator.setCurrentMonthRememberCount((int) currentMonthRememberCount);
        
        if (currentMonthRecords.size() > 0) {
            BigDecimal currentMonthRememberRate = new BigDecimal(currentMonthRememberCount)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(currentMonthRecords.size()), 2, RoundingMode.HALF_UP);
            keyIndicator.setCurrentMonthRememberRate(currentMonthRememberRate);
        } else {
            keyIndicator.setCurrentMonthRememberRate(BigDecimal.ZERO);
        }
        
        // 最近30天统计数据
        keyIndicator.setRecent30DaysTotalReviews(recent30DaysRecords.size());
        long recent30DaysRememberCount = recent30DaysRecords.stream()
                .filter(record -> record.getResult() != null && record.getResult() == PracticeResult.REMEMBER)
                .count();
        keyIndicator.setRecent30DaysRememberCount((int) recent30DaysRememberCount);
        
        if (recent30DaysRecords.size() > 0) {
            BigDecimal recent30DaysRememberRate = new BigDecimal(recent30DaysRememberCount)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(recent30DaysRecords.size()), 2, RoundingMode.HALF_UP);
            keyIndicator.setRecent30DaysRememberRate(recent30DaysRememberRate);
        } else {
            keyIndicator.setRecent30DaysRememberRate(BigDecimal.ZERO);
        }
        
        return keyIndicator;
    }
}
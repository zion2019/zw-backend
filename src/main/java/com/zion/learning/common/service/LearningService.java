package com.zion.learning.common.service;

import com.zion.common.vo.learning.request.KnowledgePointQO;
import com.zion.common.vo.learning.response.LearnReviewTodayVO;
import com.zion.common.vo.learning.response.SubjectVO;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import com.zion.learning.knowledge.service.KnowledgePointService;
import com.zion.learning.subject.service.SubjectService;
import com.zion.learning.subject.model.Subject;
import com.zion.learning.knowledge.model.KnowledgePoint;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LearningService {

    @Resource
    private KnowledgePointService knowledgePointService;

    @Resource
    private SubjectService subjectService;

    /**
     * to day window info
     * @param currentUserId curr user id
     * @return vo
     */
    public LearnReviewTodayVO getTodayReviewInfo(Long currentUserId) {
        // 1. 查询 review time <= now date 23:59:59 的所有 knowledge point,为效率考虑，只查询 subject id
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                
        // Create a condition to query knowledge points by user ID
        KnowledgePointQO knowledgePointQO = new KnowledgePointQO();
        knowledgePointQO.setUserId(currentUserId);
        
        List<KnowledgePointVO> knowledgePointVOs = knowledgePointService.list(knowledgePointQO);
        
        // Filter knowledge points that need review by end of today
        List<KnowledgePointVO> pointsToReview = knowledgePointVOs.stream()
                .filter(kp -> kp.getNextReviewTime() != null && kp.getNextReviewTime().isBefore(endOfToday))
                .collect(Collectors.toList());
        
        // 2. 排序并取得前三的 subject id
        Map<Long, Long> subjectCountMap = new HashMap<>();
        for (KnowledgePointVO kp : pointsToReview) {
            Long subjectId = kp.getSubjectId();
            subjectCountMap.put(subjectId, subjectCountMap.getOrDefault(subjectId, 0L) + 1);
        }
        
        // Sort subjects by count in descending order and limit to top 3
        List<Long> topSubjectIds = subjectCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // 3. 查询 subject 填充返回
        List<SubjectVO> subjectVOs = new ArrayList<>();
        if (!topSubjectIds.isEmpty()) {
            // Create a subject condition to query subjects by user ID and specific IDs
            Subject subjectCondition = Subject.builder()
                    .userId(currentUserId)
                    .build();
            // Set the IDs to filter by
            subjectCondition.ids(topSubjectIds);
            
            // Get the subjects
            subjectVOs = subjectService.list(subjectCondition);
        }
        
        LearnReviewTodayVO vo = new LearnReviewTodayVO();
        vo.setReviewSubjects(subjectVOs);
        return vo;
    }
}
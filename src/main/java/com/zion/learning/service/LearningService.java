package com.zion.learning.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.zion.common.vo.learning.request.SubjectQO;
import com.zion.common.vo.learning.response.LearnReviewTodayVO;
import com.zion.common.vo.learning.response.SubjectVO;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        LearnReviewTodayVO todayVO = new LearnReviewTodayVO();
        // find early top subjectId
        List<KnowledgePointVO> earlyReviewBySubjects = knowledgePointService
                .getEarlyReviewBySubjects(currentUserId, 3);
        if(CollUtil.isEmpty(earlyReviewBySubjects)){
            return todayVO;
        }

        // find subject
        SubjectQO subjectQO = new SubjectQO();
        subjectQO.setSubjectIds(earlyReviewBySubjects.stream()
                .map(KnowledgePointVO::getSubjectId).collect(Collectors.toList()));
        List<SubjectVO> subjectVOs = subjectService.list(subjectQO);

        // calculate delay days
        subjectVOs.forEach(subjectVO -> earlyReviewBySubjects.stream()
                .filter(knowledgePointVO -> knowledgePointVO.getSubjectId().equals(subjectVO.getId()))
                .findFirst()
                .ifPresent(knowledgePoint ->
                        subjectVO.setDelayDays(
                                LocalDateTimeUtil.between(LocalDateTimeUtil.beginOfDay(LocalDateTime.now())
                                , LocalDateTimeUtil.beginOfDay(knowledgePoint.getNextReviewTime())
                                , ChronoUnit.DAYS)
                        )
                ));

        LearnReviewTodayVO vo = new LearnReviewTodayVO();
        vo.setReviewSubjects(subjectVOs);
        return vo;
    }
}
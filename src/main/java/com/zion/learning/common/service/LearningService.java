package com.zion.learning.common.service;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.KnowledgePointQO;
import com.zion.common.vo.learning.request.SubjectQO;
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
        LearnReviewTodayVO todayVO = new LearnReviewTodayVO();
        // find early top subjectId
        List<Long> topSubjectIds = knowledgePointService.getReviewTopSubjectIds(currentUserId,3);
        if(CollUtil.isEmpty(topSubjectIds)){
            return todayVO;
        }

        // find subject
        SubjectQO subjectQO = new SubjectQO();
        subjectQO.setSubjectIds(topSubjectIds);
        List<SubjectVO> subjectVOs = subjectService.list(subjectQO);

        LearnReviewTodayVO vo = new LearnReviewTodayVO();
        vo.setReviewSubjects(subjectVOs);
        return vo;
    }
}
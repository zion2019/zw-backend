package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.db.ZCondition;
import com.zion.common.db.ZOrder;
import com.zion.common.vo.learning.response.LearnStrategyNextVO;
import com.zion.learning.service.KnowledgePointService;
import com.zion.learning.model.KnowledgePoint;
import com.zion.learning.dao.KnowledgePointDao;
import com.zion.learning.mapper.KnowledgePointMapper;
import com.zion.learning.common.constents.PracticeResult;
import com.zion.learning.service.LearningStrategyService;
import com.zion.learning.service.StageService;
import com.zion.common.vo.learning.request.KnowledgePointQO;
import com.zion.common.vo.learning.request.StageQO;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KnowledgePointServiceImpl implements KnowledgePointService {
    
    @Resource
    private KnowledgePointDao dao;
    
    @Resource
    private LearningStrategyService learningStrategyService;
    
    @Resource
    private StageService stageService;

    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(KnowledgePointQO qo) {
        KnowledgePoint knowledgePoint = KnowledgePointMapper.INSTANCE.toEntity(qo);

        // 计算下次复习
        LearnStrategyNextVO nextVo = learningStrategyService.calculateNext(knowledgePoint.getStrategyId()
                ,knowledgePoint.getCurrStrategyIntervalId());
        if(!nextVo.isFinished()){
            knowledgePoint.setCurrStrategyIntervalId(nextVo.getNextStrategyIntervalId());
            knowledgePoint.setNextReviewTime(nextVo.getNextReviewTime());
            knowledgePoint.setMasteryLevelCode(nextVo.getMasteryLevel().getCode());
        }

        dao.save(knowledgePoint);
        if (knowledgePoint.getStageId() != null) {
            refreshStageKnowledgePointCnt(knowledgePoint.getStageId());
        }

        return true;
    }
    
    @Override
    public KnowledgePointVO info(Long id, Long userId) {
        KnowledgePoint knowledgePoint = dao.getById(id);
        if (knowledgePoint == null) {
            return null;
        }
        return KnowledgePointMapper.INSTANCE.toVO(knowledgePoint);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 先查询知识点信息，用于后续更新stage和subject中的知识点数量
        KnowledgePoint knowledgePoint = dao.getById(id);
        if(knowledgePoint == null){
            log.error("The knowledge point:{} is not found", id);
            throw new RuntimeException("The knowledge point is not found");
        }
        dao.deleteById(id);
        refreshStageKnowledgePointCnt(knowledgePoint.getStageId());
        
        return true;
    }
    
    @Override
    public Page<KnowledgePointVO> page(KnowledgePointQO qo) {
        Page<KnowledgePointVO> pageRes = new Page<>();
        Page<KnowledgePoint> pages = dao.queryPage(
            new Page<>(qo.getPageNo(), qo.getPageSize()), new ZCondition<KnowledgePoint>()
                        .eq(qo.getStageId() != null,KnowledgePoint::getStageId, qo.getStageId())
                        .eq(qo.getSubjectId() != null,KnowledgePoint::getStageId, qo.getStageId())
                        .like(CharSequenceUtil.isNotBlank(qo.getTitle()),KnowledgePoint::getTitle, qo.getTitle()));
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(KnowledgePointMapper.INSTANCE.toVOs(pages.getDataList()));
        return pageRes;
    }
    
    @Override
    public List<KnowledgePointVO> list(KnowledgePointQO qo) {
        List<KnowledgePoint> knowledgePoints = dao.queryList(new ZCondition<KnowledgePoint>()
                        .eq(qo.getStageId() != null,KnowledgePoint::getStageId, qo.getStageId())
                        .eq(qo.getSubjectId() != null,KnowledgePoint::getStageId, qo.getStageId())
                        .like(CharSequenceUtil.isNotBlank(qo.getTitle()),KnowledgePoint::getTitle, qo.getTitle()));
        return KnowledgePointMapper.INSTANCE.toVOs(knowledgePoints);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void practice(Long knowledgePointId, PracticeResult result, Long userId) {
        // 获取知识点
        KnowledgePoint knowledgePoint = dao.getById(knowledgePointId);
        if (knowledgePoint == null ) {
            log.error("The knowledge point:{} is not found", knowledgePointId);
            throw new RuntimeException("The knowledge point is not found");
        }

        // 计算下次复习
        LearnStrategyNextVO nextVo = learningStrategyService.calculateNext(knowledgePoint.getStrategyId()
            ,knowledgePoint.getCurrStrategyIntervalId());
        if(!nextVo.isFinished()){
            // 更新知识点
            knowledgePoint.setCurrStrategyIntervalId(nextVo.getNextStrategyIntervalId());
            knowledgePoint.setNextReviewTime(nextVo.getNextReviewTime());
            knowledgePoint.setMasteryLevelCode(nextVo.getMasteryLevel().getCode());
        }

        // 更新知识点
        dao.save(knowledgePoint);
    }

    @Override
    public void updateBreakDownCnt(Long knowledgePointId, Integer breakDownCnt) {
        KnowledgePoint knowledgePoint = dao.getById(knowledgePointId);
        if (knowledgePoint == null){
            log.error("The knowledge point:{} is not found", knowledgePointId);
            throw new ServiceException("The knowledge point is not found");
        }

        knowledgePoint.setBreakdownCount(breakDownCnt);
        dao.save(knowledgePoint);

    }

    @Override
    public List<KnowledgePointVO> getEarlyReviewBySubjects(Long currentUserId, int count) {
        // today last
        LocalDateTime endOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        // query pages
        Page<KnowledgePoint> pageResult = dao.queryPage(
                new Page<>(1, 1000), new ZCondition<KnowledgePoint>()
                .le(KnowledgePoint::getNextReviewTime, endOfToday)
                .select(KnowledgePoint::getSubjectId,KnowledgePoint::getNextReviewTime)
                .order(KnowledgePoint::getNextReviewTime, ZOrder.ASC));
        if(pageResult == null || pageResult.getDataList() == null){
            return ListUtil.empty();
        }

        // find early knowledge point for count
        List<Long> earlySubjectIds = pageResult.getDataList().stream()
                .sorted(Comparator.comparing(KnowledgePoint::getNextReviewTime))
                .map(KnowledgePoint::getSubjectId)
                .distinct()
                .limit(count).collect(Collectors.toList());

        List<KnowledgePointVO> earlyKnowledgePointVOS = new ArrayList<>(count);
        for (Long earlySubjectId : earlySubjectIds) {
            KnowledgePoint kp = pageResult.getDataList().stream()
                    .filter(k -> k.getSubjectId().equals(earlySubjectId))
                    .min(Comparator.comparing(KnowledgePoint::getNextReviewTime))
                    .orElse(null);
            if(kp == null){
                continue;
            }
            earlyKnowledgePointVOS.add(KnowledgePointMapper.INSTANCE.toVO(kp));
        }

        return earlyKnowledgePointVOS;
    }

    /**
     * 更新阶段中的知识点计数
     *
     * @param stageId 阶段ID
     */
    private void refreshStageKnowledgePointCnt(Long stageId) {
        StageQO refreshStageQO = new StageQO();
        refreshStageQO.setId(stageId);
        refreshStageQO.setKnowledgePointCount(dao.count(new ZCondition<KnowledgePoint>()
                .eq(KnowledgePoint::getStageId, stageId)));
        stageService.refreshStats(refreshStageQO);
    }
}
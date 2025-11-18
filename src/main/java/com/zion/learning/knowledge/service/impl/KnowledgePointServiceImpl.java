package com.zion.learning.knowledge.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.vo.learning.response.LearnStrategyNextVO;
import com.zion.learning.knowledge.dao.KnowledgePointCondition;
import com.zion.learning.knowledge.service.KnowledgePointService;
import com.zion.learning.knowledge.model.KnowledgePoint;
import com.zion.learning.knowledge.dao.KnowledgePointDao;
import com.zion.learning.knowledge.mapper.KnowledgePointMapper;
import com.zion.learning.common.constents.PracticeResult;
import com.zion.learning.strategy.service.LearningStrategyService;
import com.zion.learning.stage.service.StageService;
import com.zion.common.vo.learning.request.KnowledgePointQO;
import com.zion.common.vo.learning.request.StageQO;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class KnowledgePointServiceImpl implements KnowledgePointService {
    
    @Resource
    private KnowledgePointDao knowledgePointDao;
    
    @Resource
    private LearningStrategyService learningStrategyService;
    
    @Resource
    private StageService stageService;

    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(KnowledgePointQO qo) {
        KnowledgePoint knowledgePoint = buildConditionFromQO(qo);

        // 计算下次复习
        LearnStrategyNextVO nextVo = learningStrategyService.calculateNext(knowledgePoint.getStrategyId()
                ,knowledgePoint.getCurrStrategyIntervalId());
        if(!nextVo.isFinished()){
            knowledgePoint.setCurrStrategyIntervalId(nextVo.getNextStrategyIntervalId());
            knowledgePoint.setNextReviewTime(nextVo.getNextReviewTime());
            knowledgePoint.setMasteryLevelCode(nextVo.getMasteryLevel().getCode());
        }

        knowledgePointDao.save(knowledgePoint);
        if (knowledgePoint.getStageId() != null) {
            refreshStageKnowledgePointCnt(knowledgePoint.getStageId());
        }

        return true;
    }
    
    @Override
    public KnowledgePointVO info(Long id, Long userId) {
        KnowledgePoint condition = KnowledgePoint.builder().userId(userId).build();
        condition.setId(id);
        condition.setUserId(userId);
        KnowledgePoint knowledgePoint = knowledgePointDao.conditionOne(condition);
        if (knowledgePoint == null) {
            return null;
        }
        return KnowledgePointMapper.INSTANCE.toVO(knowledgePoint);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 先查询知识点信息，用于后续更新stage和subject中的知识点数量
        KnowledgePoint condition = KnowledgePoint.builder().build();
        condition.setId( id);
        KnowledgePoint knowledgePoint = knowledgePointDao.conditionOne(condition);
        if(knowledgePoint == null){
            log.error("The knowledge point:{} is not found", id);
            throw new RuntimeException("The knowledge point is not found");
        }
        knowledgePointDao.delete(condition);
        refreshStageKnowledgePointCnt(knowledgePoint.getStageId());
        
        return true;
    }
    
    @Override
    public Page<KnowledgePointVO> page(KnowledgePointQO qo) {
        Page<KnowledgePointVO> pageRes = new Page<>();
        Page<KnowledgePoint> pages = knowledgePointDao.pageQuery(
            new Page<>(qo.getPageNo(), qo.getPageSize()), 
            KnowledgePoint.class, 
            buildConditionFromQO(qo));
            
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
        knowledgePointDao.condition(buildConditionFromQO(qo),buildConditionFromQO())
        List<KnowledgePoint> knowledgePoints = knowledgePointDao.condition(condition);
        return KnowledgePointMapper.INSTANCE.toVOs(knowledgePoints);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void practice(Long knowledgePointId, PracticeResult result, Long userId) {
        // 获取知识点
        KnowledgePoint knowledgePoint = knowledgePointDao.getById(knowledgePointId);
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
        knowledgePointDao.save(knowledgePoint);
    }

    @Override
    public void updateBreakDownCnt(Long knowledgePointId, Integer breakDownCnt) {
        KnowledgePoint condition = KnowledgePoint.builder().build();
        condition.setId(knowledgePointId);
        KnowledgePoint knowledgePoint = knowledgePointDao.getById(knowledgePointId);
        if (knowledgePoint == null){
            log.error("The knowledge point:{} is not found", knowledgePointId);
            throw new ServiceException("The knowledge point is not found");
        }

        knowledgePoint.setBreakdownCount(breakDownCnt);
        knowledgePointDao.save(knowledgePoint);

    }

    /**
     * 更新阶段中的知识点计数
     *
     * @param stageId 阶段ID
     */
    private void refreshStageKnowledgePointCnt(Long stageId) {
        StageQO refreshStageQO = new StageQO();
        refreshStageQO.setId(stageId);
        refreshStageQO.setKnowledgePointCount(knowledgePointDao.conditionCount(KnowledgePoint.builder().stageId(stageId).build()));
        stageService.refreshStats(refreshStageQO);
    }

    
    /**
     * 构建查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private KnowledgePoint buildConditionFromQO(KnowledgePointQO qo) {
        KnowledgePoint condition = KnowledgePoint.builder().build();
        condition.setId(qo.getId());
        condition.setTitle(qo.getTitle());
        condition.setStageId(qo.getStageId());
        condition.setSubjectId(qo.getSubjectId());
        condition.setUserId(qo.getUserId());
        condition.setBreakdownCount(qo.getBreakdownCount());
        condition.setNextReviewTime(qo.getNextReviewTime());
        return condition;
    }

    /**
     * 构建额外查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private KnowledgePointCondition buildExtendConditionFromQO(KnowledgePointQO qo) {
        KnowledgePointCondition extendCondition = KnowledgePointCondition.builder().build();
        condition.setId(qo.getId());
        condition.setTitle(qo.getTitle());
        condition.setStageId(qo.getStageId());
        condition.setSubjectId(qo.getSubjectId());
        condition.setUserId(qo.getUserId());
        condition.setBreakdownCount(qo.getBreakdownCount());
        condition.setNextReviewTime(qo.getNextReviewTime());
        return extendCondition;
    }
}
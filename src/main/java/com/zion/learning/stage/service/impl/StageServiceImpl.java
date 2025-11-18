package com.zion.learning.stage.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.learning.request.SubjectQO;
import com.zion.learning.stage.service.StageService;
import com.zion.learning.stage.model.Stage;
import com.zion.learning.stage.dao.StageDao;
import com.zion.learning.stage.mapper.StageMapper;
import com.zion.common.vo.learning.request.StageQO;
import com.zion.common.vo.learning.response.StageVO;
import com.zion.learning.subject.service.SubjectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class StageServiceImpl implements StageService {
    
    @Resource
    private StageDao stageDao;
    
    @Resource
    private SubjectService subjectService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(StageQO qo) {
        Stage stage = buildConditionFromQO(qo);
        BaseEntityUtil.filedBasicInfo(stage);
        stageDao.save(stage);
        
        // 更新科目中的阶段计数
        if (stage.getSubjectId() != null) {
            refreshSubjectStageCnt(stage.getSubjectId());
        }
        
        return true;
    }

    /**
     * 更新科目中的阶段计数
     *
     * @param subjectId 阶段信息
     */
    private void refreshSubjectStageCnt(Long subjectId) {
        SubjectQO refereshSubjectQO = new SubjectQO();
        refereshSubjectQO.setId(subjectId);
        refereshSubjectQO.setStageCount(stageDao.conditionCount(Stage.builder().subjectId(subjectId).build()));
        subjectService.refreshStats(refereshSubjectQO);
    }

    @Override
    public StageVO info(Long id, Long userId) {
        Stage condition = Stage.builder().build();
        condition.setId(id);
        Stage stage = stageDao.conditionOne(condition);
        if (stage == null) {
            return null;
        }
        return StageMapper.INSTANCE.toVO(stage);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 先查询阶段信息，用于后续更新科目阶段计数
        Stage condition = Stage.builder().build();
        condition.setId(id);
        Stage stage = stageDao.conditionOne(condition);
        if (stage != null) {
            stageDao.delete(condition);
            // 更新科目中的阶段计数
            if (stage.getSubjectId() != null) {
                refreshSubjectStageCnt(stage.getSubjectId());
            }
        }
        
        return true;
    }
    
    @Override
    public Page<StageVO> page(StageQO qo) {
        Page<StageVO> pageRes = new Page<>();
        Page<Stage> pages = stageDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), Stage.class, buildConditionFromQO(qo));
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(StageMapper.INSTANCE.toVOs(pages.getDataList()));
        return pageRes;
    }
    
    @Override
    public List<StageVO> list(Stage condition) {
        List<Stage> stages = stageDao.condition(condition);
        return StageMapper.INSTANCE.toVOs(stages);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refreshStats(StageQO qo) {
        // 更新阶段中的知识点计数
        Stage condition = Stage.builder().build();
        condition.setId(qo.getId());
        Stage stage = stageDao.conditionOne(condition);
        if (stage == null) {
            log.error("The stage:{} is not found", qo.getId());
            throw new ServiceException("The stage is not found");
        }
        
        stage.setKnowledgePointCount(qo.getKnowledgePointCount());
        stageDao.save(stage);

        // 同步刷新subject中冗余数量
        SubjectQO refereshSubjectQO = new SubjectQO();
        refereshSubjectQO.setId(stage.getSubjectId());
        refereshSubjectQO.setKnowledgePointCount(qo.getKnowledgePointCount());
        subjectService.refreshStats(refereshSubjectQO);

        return true;
    }

    /**
     * 构建查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private Stage buildConditionFromQO(StageQO qo) {
        Stage condition = Stage.builder().build();
        condition.setId(qo.getId());
        condition.setTitle(qo.getTitle());
        condition.setSubjectId(qo.getSubjectId());
        condition.setOrderNum(qo.getOrderNum());
        condition.setKnowledgePointCount(qo.getKnowledgePointCount());
        return condition;
    }
}
package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.BaseEntity;
import com.zion.common.basic.Page;
import com.zion.common.db.ZCondition;
import com.zion.learning.service.KnowledgePointService;
import com.zion.learning.service.PracticeService;
import com.zion.learning.model.PracticeRecord;
import com.zion.learning.dao.PracticeRecordDao;
import com.zion.learning.mapper.PracticeRecordMapper;
import com.zion.learning.service.EbbinghausService;
import com.zion.common.vo.learning.request.PracticeRecordQO;
import com.zion.common.vo.learning.response.PracticeRecordVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PracticeServiceImpl implements PracticeService {
    
    @Resource
    private PracticeRecordDao practiceRecordDao;
    
    @Resource
    private KnowledgePointService knowledgePointService;


    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean practice(PracticeRecordQO qo) {
        PracticeRecord practiceRecord = PracticeRecordMapper.INSTANCE.toEntity(qo);
        practiceRecord.setId(qo.getId());

        // 知识点练习触发
        knowledgePointService.practice(qo.getKnowledgePointId(), qo.getResult(), qo.getUserId());

        practiceRecordDao.save(practiceRecord);
        return true;
    }
    
    @Override
    public PracticeRecordVO info(Long id, Long userId) {
        ZCondition<PracticeRecord> condition = new ZCondition<>();
        condition.eq(BaseEntity::getId, id);
        condition.eq(PracticeRecord::getUserId, userId);
        PracticeRecord practiceRecord = practiceRecordDao.queryOne(condition);
        if (practiceRecord == null) {
            return null;
        }
        return PracticeRecordMapper.INSTANCE.toVO(practiceRecord);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        practiceRecordDao.deleteById(id);
        return true;
    }
    
    @Override
    public Page<PracticeRecordVO> page(PracticeRecordQO qo) {
        ZCondition<PracticeRecord> condition = new ZCondition<>();
        if (qo.getUserId() != null) {
            condition.eq(PracticeRecord::getUserId, qo.getUserId());
        }
        if (qo.getSubjectId() != null) {
            condition.eq(PracticeRecord::getSubjectId, qo.getSubjectId());
        }
        if (qo.getKnowledgePointId() != null) {
            condition.eq(PracticeRecord::getKnowledgePointId, qo.getKnowledgePointId());
        }
        if (qo.getResult() != null) {
            condition.eq(PracticeRecord::getResult, qo.getResult());
        }

        Page<PracticeRecordVO> pageRes = new Page<>();
        Page<PracticeRecord> pages = practiceRecordDao.queryPage(new Page<>(qo.getPageNo(), qo.getPageSize()), condition);
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(PracticeRecordMapper.INSTANCE.toVOs(pages.getDataList()));
        return pageRes;
    }
    
    @Override
    public List<PracticeRecordVO> list(PracticeRecord condition) {
        ZCondition<PracticeRecord> zCondition = new ZCondition<>();
        if (condition.getUserId() != null) {
            zCondition.eq(PracticeRecord::getUserId, condition.getUserId());
        }
        if (condition.getSubjectId() != null) {
            zCondition.eq(PracticeRecord::getSubjectId, condition.getSubjectId());
        }
        if (condition.getKnowledgePointId() != null) {
            zCondition.eq(PracticeRecord::getKnowledgePointId, condition.getKnowledgePointId());
        }
        if (condition.getResult() != null) {
            zCondition.eq(PracticeRecord::getResult, condition.getResult());
        }
        List<PracticeRecord> practiceRecords = practiceRecordDao.queryList(zCondition);
        return PracticeRecordMapper.INSTANCE.toVOs(practiceRecords);
    }


}
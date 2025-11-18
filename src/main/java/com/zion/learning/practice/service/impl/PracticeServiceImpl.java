package com.zion.learning.practice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.learning.knowledge.service.KnowledgePointService;
import com.zion.learning.practice.service.PracticeService;
import com.zion.learning.practice.model.PracticeRecord;
import com.zion.learning.practice.dao.PracticeRecordDao;
import com.zion.learning.practice.mapper.PracticeRecordMapper;
import com.zion.learning.common.service.EbbinghausService;
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
    
    @Resource
    private EbbinghausService ebbinghausService;


    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean practice(PracticeRecordQO qo) {
        PracticeRecord practiceRecord = PracticeRecord.builder()
                .userId(qo.getUserId())
                .subjectId(qo.getSubjectId())
                .knowledgePointId(qo.getKnowledgePointId())
                .result(qo.getResult())
                .practiceStartTime(qo.getPracticeStartTime())
                .practiceEndTime(qo.getPracticeEndTime())
                .build();
        practiceRecord.setId(qo.getId());

        // 知识点练习触发
        knowledgePointService.practice(qo.getKnowledgePointId(), qo.getResult(), qo.getUserId());

        practiceRecordDao.save(practiceRecord);
        return true;
    }
    
    @Override
    public PracticeRecordVO info(Long id, Long userId) {
        PracticeRecord condition = PracticeRecord.builder().userId(userId).build();
        condition.setId(id);
        condition.setUserId(userId);
        PracticeRecord practiceRecord = practiceRecordDao.conditionOne(condition);
        if (practiceRecord == null) {
            return null;
        }
        return PracticeRecordMapper.INSTANCE.toVO(practiceRecord);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        PracticeRecord condition = PracticeRecord.builder().build();
        condition.setId(id);
        practiceRecordDao.delete(condition);
        return true;
    }
    
    @Override
    public Page<PracticeRecordVO> page(PracticeRecordQO qo) {
        PracticeRecord condition = PracticeRecord.builder()
                .userId(qo.getUserId())
                .subjectId(qo.getSubjectId())
                .knowledgePointId(qo.getKnowledgePointId())
                .result(qo.getResult())
                .build();

        Page<PracticeRecordVO> pageRes = new Page<>();
        Page<PracticeRecord> pages = practiceRecordDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), PracticeRecord.class, condition);
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
        List<PracticeRecord> practiceRecords = practiceRecordDao.condition(condition);
        return PracticeRecordMapper.INSTANCE.toVOs(practiceRecords);
    }


}
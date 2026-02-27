package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.db.ZCondition;
import com.zion.common.vo.learning.request.SubjectQO;
import com.zion.common.vo.learning.response.SubjectVO;
import com.zion.learning.mapper.SubjectMapper;
import com.zion.learning.service.SubjectService;
import com.zion.learning.model.Subject;
import com.zion.learning.dao.SubjectDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubjectServiceImpl implements SubjectService {
    
    @Resource
    private SubjectDao subjectDao;

    @Override
    public SubjectVO getTodayReviewList(Long currentUserId) {
        // todo 今日待复习科目列表
        return null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SubjectQO qo) {
        Subject subject = SubjectMapper.INSTANCE.toEntity(qo);
        subjectDao.save(subject);
        return true;
    }
    
    @Override
    public SubjectVO info(Long id, Long userId) {
        Subject subject = subjectDao.getById(id);
        if(subject == null){
            return null;
        }
        return SubjectMapper.INSTANCE.toVO(subject);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        subjectDao.deleteById(id);
        return true;
    }
    
    @Override
    public Page<SubjectVO> page(SubjectQO qo) {
        Page<SubjectVO> pageRes = new Page<>();
        Page<Subject> pages = subjectDao.queryPage(
            new Page<>(qo.getPageNo(), qo.getPageSize()), 
            new ZCondition<Subject>()
                .eq(qo.getId() != null, Subject::getId, qo.getId())
                .like(qo.getTitle() != null, Subject::getTitle, qo.getTitle())
                .eq(qo.getTagId() != null, Subject::getTagId, qo.getTagId())
                .eq(qo.getUserId() != null, Subject::getUserId, qo.getUserId())
        );
        
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(SubjectMapper.INSTANCE.toVOs(pages.getDataList()));
        return pageRes;
    }
    
    @Override
    public List<SubjectVO> list(SubjectQO qo) {
        List<Subject> subjects = subjectDao.queryList(
            new ZCondition<Subject>()
                .eq(qo.getId() != null, Subject::getId, qo.getId())
                .like(qo.getTitle() != null, Subject::getTitle, qo.getTitle())
                .eq(qo.getTagId() != null, Subject::getTagId, qo.getTagId())
                .eq(qo.getUserId() != null, Subject::getUserId, qo.getUserId())
        );
        
        if (CollUtil.isEmpty(subjects)) {
            return CollUtil.newArrayList();
        }
        
        return subjects.stream()
                .map(SubjectMapper.INSTANCE::toVO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refreshStats(SubjectQO qo) {
        // 更新科目中的统计信息
        Subject subject = subjectDao.getById(qo.getId());
        if(subject == null){
            log.error("The subject:{} is not found", qo.getId());
            throw new ServiceException("The subject is not found");
        }

        if (qo.getStageCount() != null) {
            subject.setStageCount(qo.getStageCount());
        }
        
        if (qo.getKnowledgePointCount() != null) {
            subject.setKnowledgePointCount(qo.getKnowledgePointCount());
        }
        
        subjectDao.save(subject);
        return true;
    }
}
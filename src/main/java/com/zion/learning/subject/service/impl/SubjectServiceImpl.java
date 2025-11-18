package com.zion.learning.subject.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.learning.request.SubjectQO;
import com.zion.common.vo.learning.response.SubjectVO;
import com.zion.learning.subject.mapper.SubjectMapper;
import com.zion.learning.subject.service.SubjectService;
import com.zion.learning.subject.model.Subject;
import com.zion.learning.subject.dao.SubjectDao;
import com.zion.learning.stage.dao.StageDao;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubjectServiceImpl implements SubjectService {
    
    @Resource
    private SubjectDao subjectDao;
    
    @Resource
    private StageDao stageDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SubjectQO qo) {
        subjectDao.save(buildConditionFromQO(qo));
        return true;
    }
    
    @Override
    public SubjectVO info(Long id, Long userId) {
        Subject condition = Subject.builder().build();
        condition.setId(id);
        condition.setUserId(userId);
        Subject subject = subjectDao.conditionOne(condition);
        if(subject == null){
            return null;
        }
        return SubjectMapper.INSTANCE.toVO(subject);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Subject condition = Subject.builder().build();
        condition.setId(id);
        subjectDao.delete(condition);
        return true;
    }
    
    @Override
    public Page<SubjectVO> page(SubjectQO qo) {
        Page<SubjectVO> pageRes = new Page<>();
        Page<Subject> pages = subjectDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), Subject.class, buildConditionFromQO(qo));
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
    public List<SubjectVO> list(Subject condition) {
        List<Subject> subjects = subjectDao.condition(condition);
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
        Subject condition = Subject.builder().build();
        condition.setId(qo.getId());
        Subject subject = subjectDao.conditionOne(condition);
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

    /**
     * 构建查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private Subject buildConditionFromQO(SubjectQO qo) {
        Subject condition = Subject.builder().build();
        condition.setId(qo.getId());
        condition.setTitle(qo.getTitle());
        condition.setTagId(qo.getTagId());
        condition.setUserId(qo.getUserId());
        return condition;
    }
}
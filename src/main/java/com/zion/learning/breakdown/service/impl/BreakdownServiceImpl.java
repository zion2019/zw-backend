package com.zion.learning.breakdown.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.learning.request.BreakdownQO;
import com.zion.common.vo.learning.response.BreakdownVO;
import com.zion.learning.breakdown.mapper.BreakdownMapper;
import com.zion.learning.breakdown.service.BreakdownService;
import com.zion.learning.breakdown.model.Breakdown;
import com.zion.learning.breakdown.dao.BreakdownDao;
import com.zion.learning.knowledge.service.KnowledgePointService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class BreakdownServiceImpl implements BreakdownService {
    
    @Resource
    private BreakdownDao breakdownDao;

    @Resource
    private KnowledgePointService knowledgePointService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(BreakdownQO qo) {
        Breakdown entity = BreakdownMapper.INSTANCE.toEntity(qo);
        breakdownDao.save(entity);

        refreshKnowledgePointCnt(entity.getKnowledgePointId());
        return true;
    }
    
    @Override
    public BreakdownVO info(Long id, Long userId) {
        Breakdown condition = Breakdown.builder().build();
        condition.setId(id);
        Breakdown breakdown = breakdownDao.conditionOne(condition);
        if(breakdown == null){
            return null;
        }
        return BreakdownMapper.INSTANCE.toVO(breakdown);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Breakdown breakdown = breakdownDao.getById(id);
        if (breakdown == null){
            log.error("breakdown not exist:{}",id);
            throw new ServiceException("breakdown not exist");
        }

        Breakdown condition = Breakdown.builder().build();
        condition.setId(id);
        breakdownDao.delete(condition);

        refreshKnowledgePointCnt(breakdown.getKnowledgePointId());
        return true;
    }
    
    @Override
    public Page<BreakdownVO> page(BreakdownQO qo) {
        Page<BreakdownVO> pageRes = new Page<>();
        Page<Breakdown> pages = breakdownDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), Breakdown.class, buildConditionFromQO(qo));
        if(pages == null || CollUtil.isEmpty(pages.getDataList())){
            return pageRes;
        }
        pageRes.setPageNo(pages.getPageNo());
        pageRes.setPageSize(pages.getPageSize());
        pageRes.setTotal(pages.getTotal());
        pageRes.setDataList(BreakdownMapper.INSTANCE.toVOs(pages.getDataList()));
        return pageRes;
    }
    
    @Override
    public List<BreakdownVO> list(BreakdownQO qo) {
        List<Breakdown> breakdowns = breakdownDao.condition(buildConditionFromQO(qo));
        return BreakdownMapper.INSTANCE.toVOs(breakdowns);
    }
    
    /**
     * 构建查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private Breakdown buildConditionFromQO(BreakdownQO qo) {
        Breakdown condition = Breakdown.builder().build();
        condition.setId(qo.getId());
        condition.setTitle(qo.getTitle());
        condition.setContent(qo.getContent());
        condition.setKnowledgePointId(qo.getKnowledgePointId());
        condition.setSubjectId(qo.getSubjectId());
        return condition;
    }

    /**
     * 更新知识点中的 breakdown计数
     *
     * @param knowledgePointId 知识点ID
     */
    private void refreshKnowledgePointCnt(Long knowledgePointId) {
        long breakDownCnt = breakdownDao.conditionCount(Breakdown.builder().knowledgePointId(knowledgePointId).build());
        knowledgePointService.updateBreakDownCnt(knowledgePointId,Long.valueOf(breakDownCnt).intValue());
    }
}
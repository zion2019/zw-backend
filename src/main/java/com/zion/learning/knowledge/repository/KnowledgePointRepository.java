package com.zion.learning.knowledge.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.knowledge.dao.KnowledgePointDao;
import com.zion.learning.knowledge.model.KnowledgePoint;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Repository
public class KnowledgePointRepository extends ZWMongoBasicRep<KnowledgePoint, BaseDaoQueryCondition> implements KnowledgePointDao {

    @Override
    public Query generateQuery(KnowledgePoint condition) {
        Query query = new Query();
        if (condition.getSubjectId() != null) {
            query.addCriteria(Criteria.where("subjectId").is(condition.getSubjectId()));
        }
        if (condition.getStageId() != null) {
            query.addCriteria(Criteria.where("stageId").is(condition.getStageId()));
        }
        
        // 添加userId查询条件
        if (condition.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        
        // 添加nextReviewTime查询条件（如果设置了查询今天的复习点）
        if (condition.getNextReviewTime() != null) {
            // 查询小于等于指定时间的知识点
            query.addCriteria(Criteria.where("nextReviewTime").lte(condition.getNextReviewTime()));
        }
        
        return query;
    }

}
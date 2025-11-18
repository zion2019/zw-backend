package com.zion.learning.practice.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.practice.dao.PracticeRecordDao;
import com.zion.learning.practice.model.PracticeRecord;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class PracticeRecordRepository extends ZWMongoBasicRep<PracticeRecord, BaseDaoQueryCondition> implements PracticeRecordDao {

    @Override
    public Query generateQuery(PracticeRecord condition) {
        Query query = new Query();
        if (condition.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if (condition.getSubjectId() != null) {
            query.addCriteria(Criteria.where("subjectId").is(condition.getSubjectId()));
        }
        if (condition.getKnowledgePointId() != null) {
            query.addCriteria(Criteria.where("knowledgePointId").is(condition.getKnowledgePointId()));
        }
        if (condition.getResult() != null) {
            query.addCriteria(Criteria.where("result").is(condition.getResult()));
        }
        return query;
    }
}
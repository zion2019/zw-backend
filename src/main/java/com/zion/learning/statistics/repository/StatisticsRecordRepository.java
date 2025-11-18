package com.zion.learning.statistics.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.common.basic.ZWMongoBasicRep;
import com.zion.learning.statistics.dao.StatisticsRecordDao;
import com.zion.learning.statistics.model.StatisticsRecord;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class StatisticsRecordRepository extends ZWMongoBasicRep<StatisticsRecord, BaseDaoQueryCondition> implements StatisticsRecordDao {

    @Override
    public Query generateQuery(StatisticsRecord condition) {
        Query query = new Query();
        if (condition.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if (condition.getStatisticType() != null) {
            query.addCriteria(Criteria.where("statisticType").is(condition.getStatisticType()));
        }
        return query;
    }
}
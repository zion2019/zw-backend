package com.zion.learning.strategy.repository;

import com.zion.common.basic.BaseDaoQueryCondition;
import com.zion.learning.strategy.dao.LearningStrategyIntervalDao;
import com.zion.learning.strategy.model.LearningStrategyInterval;
import com.zion.common.basic.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class LearningStrategyIntervalRepository extends ZWMongoBasicRep<LearningStrategyInterval, BaseDaoQueryCondition> implements LearningStrategyIntervalDao {

    @Override
    public Query generateQuery(LearningStrategyInterval condition) {
        Query query = new Query();
        if (condition.getStrategyId() != null) {
            query.addCriteria(Criteria.where("strategyId").is(condition.getStrategyId()));
        }
        return query;
    }
    
    public List<LearningStrategyInterval> findByStrategyId(Long strategyId) {
        LearningStrategyInterval condition = LearningStrategyInterval.builder().strategyId(strategyId).build();
        return this.condition(condition);
    }
}
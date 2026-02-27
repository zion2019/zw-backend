package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.LearningStrategyIntervalDao;
import com.zion.learning.model.LearningStrategyInterval;
import org.springframework.stereotype.Repository;

@Repository
public class LearningStrategyIntervalDaoMongoImpl extends ZDaoMongoImpl<LearningStrategyInterval> implements LearningStrategyIntervalDao {
}
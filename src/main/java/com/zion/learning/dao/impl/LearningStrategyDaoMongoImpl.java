package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.LearningStrategyDao;
import com.zion.learning.model.LearningStrategy;
import org.springframework.stereotype.Repository;

@Repository
public class LearningStrategyDaoMongoImpl extends ZDaoMongoImpl<LearningStrategy> implements LearningStrategyDao {
}
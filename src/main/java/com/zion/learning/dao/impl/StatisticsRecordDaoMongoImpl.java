package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.StatisticsRecordDao;
import com.zion.learning.model.StatisticsRecord;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsRecordDaoMongoImpl extends ZDaoMongoImpl<StatisticsRecord> implements StatisticsRecordDao {
}
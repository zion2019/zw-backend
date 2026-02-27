package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.PracticeRecordDao;
import com.zion.learning.model.PracticeRecord;
import org.springframework.stereotype.Repository;

@Repository
public class PracticeRecordDaoMongoImpl extends ZDaoMongoImpl<PracticeRecord> implements PracticeRecordDao {
}
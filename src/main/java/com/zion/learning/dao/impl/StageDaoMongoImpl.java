package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.StageDao;
import com.zion.learning.model.Stage;
import org.springframework.stereotype.Repository;

@Repository
public class StageDaoMongoImpl extends ZDaoMongoImpl<Stage> implements StageDao {
}
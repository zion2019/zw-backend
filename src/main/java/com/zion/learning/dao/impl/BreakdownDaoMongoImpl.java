package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.BreakdownDao;
import com.zion.learning.model.Breakdown;
import org.springframework.stereotype.Repository;

@Repository
public class BreakdownDaoMongoImpl extends ZDaoMongoImpl<Breakdown> implements BreakdownDao {
}
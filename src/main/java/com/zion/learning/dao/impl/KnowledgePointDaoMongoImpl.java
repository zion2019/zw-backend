package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.KnowledgePointDao;
import com.zion.learning.model.KnowledgePoint;
import org.springframework.stereotype.Repository;

@Repository
public class KnowledgePointDaoMongoImpl extends ZDaoMongoImpl<KnowledgePoint> implements KnowledgePointDao {
}
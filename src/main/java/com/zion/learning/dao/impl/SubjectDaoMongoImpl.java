package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.SubjectDao;
import com.zion.learning.model.Subject;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectDaoMongoImpl extends ZDaoMongoImpl<Subject> implements SubjectDao {
}
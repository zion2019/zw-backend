package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.TagDao;
import com.zion.learning.model.Tag;
import org.springframework.stereotype.Repository;

@Repository
public class TagDaoMongoImpl extends ZDaoMongoImpl<Tag> implements TagDao {
}
package com.zion.resource.user.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.resource.user.dao.UserDao;
import com.zion.resource.user.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoMongoImpl extends ZDaoMongoImpl<User> implements UserDao {
}
package com.zion.bill.dao.impl;

import com.zion.bill.dao.BillCategoryDao;
import com.zion.bill.model.BillCategory;
import com.zion.common.db.ZDaoMongoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class BillCategoryDaoMongoImpl extends ZDaoMongoImpl<BillCategory> implements BillCategoryDao {
}
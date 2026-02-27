package com.zion.bill.dao.impl;

import com.zion.bill.dao.BillDao;
import com.zion.bill.model.Bills;
import com.zion.common.db.ZDaoMongoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class BillDaoMongoImpl extends ZDaoMongoImpl<Bills> implements BillDao {
}
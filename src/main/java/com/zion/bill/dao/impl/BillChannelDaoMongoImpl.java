package com.zion.bill.dao.impl;

import com.zion.bill.dao.BillChannelDao;
import com.zion.bill.model.BillChannel;
import com.zion.common.db.ZDaoMongoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class BillChannelDaoMongoImpl extends ZDaoMongoImpl<BillChannel> implements BillChannelDao {
}
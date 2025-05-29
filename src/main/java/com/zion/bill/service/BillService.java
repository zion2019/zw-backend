package com.zion.bill.service;

import com.zion.bill.model.Bills;
import com.zion.common.basic.Page;
import com.zion.common.vo.bill.req.BillChartQO;
import com.zion.common.vo.bill.req.BillQO;
import com.zion.common.vo.bill.rsp.BillChartVO;
import com.zion.common.vo.bill.rsp.BillsVO;
import com.zion.common.vo.bill.rsp.CategoryVO;

import java.io.Serializable;
import java.util.List;

public interface BillService {
    void save(BillQO qo);

    List<BillsVO> list(BillQO billQO);

    Page<BillsVO> page(BillQO qo);

    BillsVO info(Long id,Long userId);

    List<CategoryVO> recentlyCategory(Integer count, Long currentUserId);

    long conditionCount(BillQO qo);

    void sendBillEmail();
}

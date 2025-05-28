package com.zion.bill.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import com.zion.bill.dao.BillDao;
import com.zion.bill.model.Bills;
import com.zion.bill.service.BillCategoryService;
import com.zion.bill.service.BillService;
import com.zion.common.basic.Page;
import com.zion.common.utils.BillDateUtil;
import com.zion.common.vo.bill.req.BillQO;
import com.zion.common.vo.bill.rsp.BillsVO;
import com.zion.common.vo.bill.rsp.CategoryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

    @Resource
    private BillDao billDao;

    @Resource
    private BillCategoryService categoryService;


    @Override
    public void save(BillQO qo) {
        Assert.isTrue(qo.getCategoryId() != null,"categoryId is required");
        Assert.isTrue(qo.getAmount() != null && qo.getAmount().compareTo(BigDecimal.ZERO) > 0,"amount is required");
        Bills bill = Bills.builder()
                .userId(qo.getUserId())
                .amount(qo.getAmount())
                .categoryId(qo.getCategoryId())
                .remark(qo.getRemark())
                .location(qo.getLocation())
                .build();
        bill.setId(qo.getId());
        billDao.save(bill);
    }

    @Override
    public List<BillsVO> list(BillQO qo) {
        qo.setPageSize(-1);
        Page<Bills> billsPage = this.pageBillEntities(qo);
        if(billsPage == null || CollUtil.isEmpty(billsPage.getDataList())){
            return List.of();
        }
        List<Bills> bills = billsPage.getDataList();
        return bills.stream().map(bill -> {
            BillsVO billsVO = new BillsVO();
            billsVO.setAmount(bill.getAmount());
            billsVO.setCategoryId(bill.getCategoryId());
            billsVO.setId(bill.getId());
            return billsVO;
        }).toList();
    }

    @Override
    public Page<BillsVO> page(BillQO qo) {
        Page<BillsVO> pageRes = new Page<>();
        Page<Bills> billsPage = pageBillEntities(qo);
        if(billsPage == null || CollUtil.isEmpty(billsPage.getDataList())){
            return pageRes;
        }

        pageRes.setPageNo(billsPage.getPageNo());
        pageRes.setPageSize(billsPage.getPageSize());
        pageRes.setTotal(billsPage.getTotal());
        List<BillsVO> billsVOList = new ArrayList<>();
        List<Bills> bills = billsPage.getDataList();
        bills.forEach(bill -> {
            BillsVO billsVO = new BillsVO();
            billsVO.setAmount(bill.getAmount());
            billsVO.setCategoryId(bill.getCategoryId());
            billsVO.setId(bill.getId());
            billsVO.setBillDate(LocalDateTimeUtil.format(bill.getCreatedTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            billsVO.setBillRemark(bill.getRemark());
            billsVO.setLocation(bill.getLocation());
            billsVOList.add(billsVO);
        });
        pageRes.setDataList(billsVOList);
        return pageRes;
    }

    @Override
    public BillsVO info(Long id,Long userId) {
        Bills bill = billDao.getById(id);
        if(bill == null){
            return null;
        }

        BillsVO billsVO = new BillsVO();
        billsVO.setAmount(bill.getAmount());
        billsVO.setCategoryId(bill.getCategoryId());
        billsVO.setId(bill.getId());
        billsVO.setLocation(bill.getLocation());
        billsVO.setBillRemark(bill.getRemark());

        // 冗余的类型名称
        CategoryVO categoryVO = categoryService.info(bill.getCategoryId(), userId);
        billsVO.setCategoryDesc(categoryVO.getTitle());

        return billsVO;
    }

    @Override
    public List<CategoryVO> recentlyCategory(Integer count, Long currentUserId) {
        // 查询用户的最近1000条账单记录
        BillQO billQO = new BillQO();
        billQO.setUserId(currentUserId);
        billQO.setPageSize(1000);  // 获取最多1000条数据，防止内存溢出

        Page<Bills> billsPage = pageBillEntities(billQO);
        if (CollUtil.isEmpty(billsPage.getDataList())) {
            return List.of();
        }

        // 按照 categoryId 分组并统计出现次数
        Map<Long, Long> categoryCountMap = billsPage.getDataList().stream()
                .filter(bill -> bill.getCategoryId() != null)
                .collect(Collectors.groupingBy(Bills::getCategoryId, Collectors.counting()));

        if (categoryCountMap.isEmpty()) {
            return List.of();
        }

        // 获取前 count 个消费次数最多的分类ID
        List<Long> topCategoryIds = categoryCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .toList();

        List<CategoryVO> categoryVOS = new ArrayList<>();
        for (Long topCategoryId : topCategoryIds) {
            CategoryVO categoryVO = categoryService.info(topCategoryId, currentUserId);
            categoryVOS.add(categoryVO);
        }

        return categoryVOS;
    }

    @Override
    public long conditionCount(BillQO qo) {
        return billDao.conditionCount( Bills.builder().categoryId(qo.getCategoryId()).userId(qo.getUserId()).build());
    }

    private Page<Bills> pageBillEntities(BillQO qo) {
        Bills condition = Bills.builder().build();
        condition.setUserId(qo.getUserId());

        // 限制日期范围
        condition.setQueryBillStartTime(BillDateUtil.parseStartDate(qo.getStartDay()));
        condition.setQueryBillEndTime(BillDateUtil.parseEndDate(qo.getEndDay()));
        condition.setQueryCategoryIdList(qo.getCategoryIdList());
        if(qo.getCategoryId() != null){
            condition.setCategoryId(qo.getCategoryId());
        }

        Page<Bills> pageRsp = billDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), Bills.class, condition);
        return pageRsp;
    }
}

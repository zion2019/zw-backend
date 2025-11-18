package com.zion.bill.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.zion.bill.dao.BillDao;
import com.zion.bill.model.BillCategory;
import com.zion.bill.model.Bills;
import com.zion.bill.service.BillCategoryService;
import com.zion.bill.service.BillChannelService;
import com.zion.bill.service.BillService;
import com.zion.common.basic.Page;
import com.zion.common.db.ZCondition;
import com.zion.common.utils.BillDateUtil;
import com.zion.common.vo.bill.req.BillQO;
import com.zion.common.vo.bill.req.CategoryQO;
import com.zion.common.vo.bill.req.ChannelQO;
import com.zion.common.vo.bill.rsp.*;
import com.zion.common.vo.resource.request.UserQO;
import com.zion.common.vo.resource.response.UserVO;
import com.zion.resource.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

    @Resource
    private BillChannelService channelService;

    @Resource
    private UserService userService;

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String configMailUserName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(BillQO qo) {
        Assert.isTrue(qo.getAmount() != null && qo.getAmount().compareTo(BigDecimal.ZERO) != 0, "金额不能为空");
        Assert.isTrue(qo.getCategoryId() != null, "分类不能为空");
        Assert.isTrue(qo.getUserId() != null, "用户ID不能为空");

        Bills bill = Bills.builder()
                .amount(qo.getAmount())
                .categoryId(qo.getCategoryId())
                .channelId(qo.getChannelId())
                .remark(qo.getRemark())
                .location(qo.getLocation())
                .userId(qo.getUserId())
                .build();

        billDao.save(bill);
    }

    @Override
    public List<BillsVO> list(BillQO billQO) {
        List<Bills> bills = billDao.queryList(buildConditionFromQO(billQO));
        return bills.stream().map(bill -> {
            BillsVO billsVO = new BillsVO();
            billsVO.setAmount(bill.getAmount());
            billsVO.setCategoryId(bill.getCategoryId());
            billsVO.setChannelId(bill.getChannelId());
            billsVO.setId(bill.getId());
            billsVO.setBillDate(LocalDateTimeUtil.format(bill.getCreatedTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            billsVO.setBillRemark(bill.getRemark());
            billsVO.setLocation(bill.getLocation());
            billsVO.setAmount(bill.getAmount());
            billsVO.setCategoryId(bill.getCategoryId());
            billsVO.setChannelId(bill.getChannelId());
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
            billsVO.setChannelId(bill.getChannelId());
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
        billsVO.setChannelId(bill.getChannelId());
        billsVO.setId(bill.getId());
        billsVO.setLocation(bill.getLocation());
        billsVO.setBillRemark(bill.getRemark());

        // 冗余的类型名称
        CategoryVO categoryVO = categoryService.info(bill.getCategoryId(), userId);
        billsVO.setCategoryDesc(categoryVO.getTitle());

        return billsVO;
    }

    @Override
    public BillRecentInfoVO recentlyInfo(Integer count, Long currentUserId) {
        BillRecentInfoVO recentInfoVO = new BillRecentInfoVO();
        // 查询用户的最近1000条账单记录
        BillQO billQO = new BillQO();
        billQO.setUserId(currentUserId);
        billQO.setPageSize(1000);  // 获取最多1000条数据，防止内存溢出

        Page<Bills> billsPage = pageBillEntities(billQO);
        if (CollUtil.isEmpty(billsPage.getDataList())) {
            return recentInfoVO;
        }

        // 统计分类使用次数
        List<CategoryVO> categoryVOS = new ArrayList<>();
        List<Long> topCategoryIds = billsPage.getDataList().stream()
                .collect(Collectors.groupingBy(Bills::getCategoryId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .toList();
        if(CollUtil.isNotEmpty(topCategoryIds)){
            categoryVOS = topCategoryIds.stream()
                    .map(id -> categoryService.info(id, currentUserId))
                    .toList();
        }
        recentInfoVO.setCategories(categoryVOS);

        // 统计渠道使用次数
        List<Long> topChannelIds = billsPage.getDataList().stream()
                .filter(bill -> bill.getChannelId() != null)
                .collect(Collectors.groupingBy(Bills::getChannelId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .toList();
        if(CollUtil.isNotEmpty(topChannelIds)){
            List<ChannelVO> channelVOS = new ArrayList<>();
            for (Long topChannelId : topChannelIds) {
                ChannelVO channelVO = channelService.info(topChannelId, currentUserId);
                channelVOS.add(channelVO);
            }
            recentInfoVO.setChannels(channelVOS);
        }
        return recentInfoVO;
    }

    @Override
    public long conditionCount(BillQO qo) {
        return billDao.count(buildConditionFromQO(qo));
    }

    private Page<Bills> pageBillEntities(BillQO qo) {
        // 限制日期范围
        LocalDateTime startTime = BillDateUtil.parseStartDate(qo.getStartDay());
        LocalDateTime endTime = BillDateUtil.parseEndDate(qo.getEndDay());

        return billDao.queryPage(
                new Page<>(qo.getPageNo(), qo.getPageSize()),
                new ZCondition<Bills>()
                    .eq(qo.getUserId() != null, Bills::getUserId, qo.getUserId())
                    .eq(qo.getCategoryId() != null, Bills::getCategoryId, qo.getCategoryId())
                    .eq(qo.getChannelId() != null, Bills::getChannelId, qo.getChannelId())
                    .ge( Bills::getCreatedTime, startTime)
                    .le( Bills::getCreatedTime, endTime)
                    .order(Bills::getCreatedTime, com.zion.common.db.ZOrder.DESC));
    }

    /**
     * 构建查询条件
     *
     * @param qo 查询参数
     * @return 查询条件
     */
    private ZCondition<Bills> buildConditionFromQO(BillQO qo) {
        ZCondition<Bills> condition = new ZCondition<>();
        if (qo.getUserId() != null) {
            condition.eq(Bills::getUserId, qo.getUserId());
        }
        if (qo.getCategoryId() != null) {
            condition.eq(Bills::getCategoryId, qo.getCategoryId());
        }
        if (qo.getChannelId() != null) {
            condition.eq(Bills::getChannelId, qo.getChannelId());
        }
        // This would need to be handled differently as ZCondition doesn't seem to support in queries directly
        // We'll handle this in the pageBillEntities method
        return condition;
    }

    @Override
    public void sendBillEmail() {

        // 已当前月份为基准，计算出上月开始时间和结束时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfLastMonth = now.minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN);
        LocalDateTime endOfLastMonth = now.withDayOfMonth(1).minusDays(1).with(LocalTime.MAX);
        
        // 获取所有分类信息
        List<BillCategory> categoryVOS = categoryService.condition(BillCategory.builder().build());

        // 根据上月开始时间和结束时间，查询出所有账单信息
        List<Bills> allBills = billDao.queryList(new ZCondition<Bills>()
                .ge( Bills::getCreatedTime, startOfLastMonth)
                .le( Bills::getCreatedTime, endOfLastMonth));


        if (CollUtil.isEmpty(allBills)) {
            log.warn("没有账单数据可发送");
            return;
        }

        Map<Long, List<Bills>> userBillsMap = allBills.stream().collect(Collectors.groupingBy(Bills::getUserId));
        Map<Long, List<BillCategory>> userCategoryMap = categoryVOS.stream().collect(Collectors.groupingBy(BillCategory::getUserId));
        for (Map.Entry<Long, List<Bills>> entry : userBillsMap.entrySet()) {
            Long userId = entry.getKey();
            List<Bills> userBills = entry.getValue();
            List<BillCategory> userCategories = userCategoryMap.get(userId);


            // 获取用户邮箱
            UserVO userVO = userService.conditionOne(UserQO.builder().id(userId).build());
            if(userVO == null){
                log.warn("用户不存在:{}",  userId);
                continue;
            }
            if(CharSequenceUtil.isBlank(userVO.getEmail())){
                log.warn("用户邮箱为空:{}",  userId);
                continue;
            }

            // 写入 Excel
            File tempFile = null;
            try{
                tempFile = File.createTempFile("bill_report_", ".xlsx");
                ExcelWriter excelWriter = EasyExcel.write(tempFile, BillsExcelVO.class).build();
                WriteSheet writeSheet = EasyExcel.writerSheet("账单明细").build();
                excelWriter.write(covertBillExcelVo(userBills), writeSheet);
                excelWriter.finish();

                // 发送邮件
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(configMailUserName);
                helper.setTo(userVO.getEmail());
                helper.setSubject(startOfLastMonth.getMonthValue() + "月账单报表");
                helper.setText(String.format(
                        "您好 %s，\n\n这是您%s月的账单明细及分类统计报表，请查收。\n\n祝好，\nZion 账单系统",
                        userVO.getUsername(),
                        startOfLastMonth.getMonthValue()
                ));
                helper.addAttachment("账单报表.xlsx", tempFile);

                javaMailSender.send(message);
                log.info("已向用户 {}({}) 发送账单报表邮件", userVO.getEmail(), userVO.getUsername());

                // 删除临时文件
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    log.warn("无法删除临时文件: {}", tempFile.getAbsolutePath());
                }
            } catch (Exception e) {
                log.error("发送邮件失败", e);
            }

        }
    }

    private List<BillsExcelVO> covertBillExcelVo(List<Bills> userBills) {
        List<BillsExcelVO> vos = new ArrayList<>();
        for (Bills bill : userBills) {
            BillsExcelVO vo = new BillsExcelVO();
            if(bill.getId() != null){
                vo.setId(String.valueOf(bill.getId()));
            }
            vo.setCreatedTime(bill.getCreatedTime());
            vo.setCreatedUser(bill.getCreatedUser());
            vo.setUpdatedTime(bill.getUpdatedTime());
            vo.setUpdatedUser(bill.getUpdatedUser());
            vo.setVersion(bill.getVersion());
            vo.setDeleted(bill.getDeleted());
            if(bill.getUserId() != null){
                vo.setUserId(String.valueOf(bill.getUserId()));
            }
            vo.setAmount(bill.getAmount());
            if(bill.getCategoryId() != null){
                vo.setCategoryId(String.valueOf(bill.getCategoryId()));
            }
            vo.setRemark(bill.getRemark());
            vo.setLocation(bill.getLocation());
            vos.add(vo);
        }
        return vos;
    }
}

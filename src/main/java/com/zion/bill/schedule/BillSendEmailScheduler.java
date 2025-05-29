package com.zion.bill.schedule;

import com.zion.bill.service.BillService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zion
 * 账单发送邮件定时任务
 */
@Component
public class BillSendEmailScheduler {

    @Resource
    private BillService billService;

    /**
     * 每月1日推送上月账单
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void sendBillEmail() {
        billService.sendBillEmail();
    }

}

package com.zion.learning.schedule;

import com.zion.learning.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * manager task schedule
 */
@Component
public class TaskSchedule {

    @Resource
    private TaskService taskService;


    @Scheduled(cron = "0 0/5 * * * ?")
    public void remind(){
        taskService.scanAndRemind();
    }

}

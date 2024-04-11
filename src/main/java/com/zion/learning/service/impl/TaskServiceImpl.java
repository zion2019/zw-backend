package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.zion.common.basic.ExpireTagTypeEnum;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTaskQO;
import com.zion.common.vo.learning.response.TaskExpireTagVo;
import com.zion.common.vo.learning.response.TaskVO;
import com.zion.common.vo.learning.response.TodoTaskVO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.common.vo.resource.request.UserQO;
import com.zion.common.vo.resource.response.UserVO;
import com.zion.learning.dao.TaskDao;
import com.zion.learning.model.Task;
import com.zion.learning.service.PushService;
import com.zion.learning.service.TaskService;
import com.zion.learning.service.TopicService;
import com.zion.resource.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    @Resource
    private TopicService topicService;

    @Resource
    private PushService pushService;

    @Resource
    private UserService userService;

    @Override
    public Page<TodoTaskVO> page(TodoTaskQO qo) {
        Page<TodoTaskVO> voPage = new Page<>();
        voPage.setPageNo(qo.getPageNo());
        voPage.setPageSize(qo.getPageSize());
        Task condition = Task.builder().userId(qo.getUserId())
                .finished(false)
                .userId(qo.getUserId())
                .build();
        // limited today task
//        if(qo.isToday()){
//            condition.setGtEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
//        }
        condition.sort("endTime", Sort.Direction.DESC);
        Page<Task> page = taskDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize())
                , Task.class, condition);

        List<TodoTaskVO> voList = new ArrayList<>(page.getPageSize());
        if(CollUtil.isNotEmpty(page.getDataList())){
            Map<Long, TopicVO> topicMap = new HashMap<>();
            List<Long> topicIdSet = page.getDataList().stream().map(Task::getTopicId).collect(Collectors.toList());
            List<TopicVO> topicInfos = topicService.getTitleByIds(topicIdSet);
            if(CollUtil.isNotEmpty(topicInfos)){
                topicMap = topicInfos.stream().collect(Collectors.toMap(TopicVO::getId, Function.identity(), (t1, t2) -> t1));
            }

            for (Task task : page.getDataList()) {
                TodoTaskVO vo = new TodoTaskVO();
                voList.add(vo);
                vo.setTaskId(task.getId());
                vo.setTitle(task.getTitle());
                // topic info
                TopicVO topicVO = topicMap.get(task.getTopicId());
                if(topicVO != null){
                    vo.setTopicFullName(topicVO.getFullTitle());
                    vo.setBackground(topicVO.getBackground());
                }

                // calculate expire
                if(!task.getFinished()){
                    // Not Start
                    if(LocalDateTime.now().isAfter(task.getStartTime())){
                        vo.setExpireTag(this.calculateExpire(task.getStartTime(),LocalDateTime.now(),"Begin With "));
                    }else{
                        if(LocalDateTime.now().isAfter(task.getEndTime())){
                            vo.setExpireTag(this.calculateExpire(LocalDateTime.now(),task.getEndTime(),"Expired "));
                        }else{
                            vo.setExpireTag(this.calculateExpire(task.getEndTime(),LocalDateTime.now(),"Last "));
                        }
                    }
                }
            }


            voPage.setDataList(voList);
        }

        return voPage;
    }


    private TaskExpireTagVo calculateExpire(LocalDateTime startTime,LocalDateTime endTime,String expireType) {
        TaskExpireTagVo vo = new TaskExpireTagVo();
        Duration remainingTime = Duration.between(startTime, endTime);

        if (remainingTime.getSeconds() < 60) {
            vo.setTagType(ExpireTagTypeEnum.danger);
            vo.setTagName(expireType+"1 minute");
        } else if (remainingTime.getSeconds() < 3600) {
            vo.setTagType(ExpireTagTypeEnum.danger);
            vo.setTagName(expireType+remainingTime.toMinutes()+" minute");
        } else if (remainingTime.getSeconds() < 86400) {
            vo.setTagType(ExpireTagTypeEnum.warning);
            vo.setTagName(expireType+remainingTime.toHours()+" hours");
        } else if (remainingTime.getSeconds() < 2592000) { // 大约一个月的秒数
            vo.setTagType(ExpireTagTypeEnum.warning);
            vo.setTagName(expireType+remainingTime.toDays()+" days");
        } else if (remainingTime.getSeconds() < 31536000) { // 大约一年的秒数
            vo.setTagType(ExpireTagTypeEnum.warning);
            vo.setTagName(expireType+(int)(remainingTime.toDays() / 30)+" month");
        } else {
            vo.setTagType(ExpireTagTypeEnum.warning);
            vo.setTagName(expireType+(int)(remainingTime.toDays() / 365)+" years");
        }
        return vo;
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean addOrEditTask(Long userId,TaskQO qo){
        Task task ;
        if(qo.getId() != null){
            task = taskDao.getById(qo.getId());
        }else{
            task = Task.builder()
                    .userId(userId)
                    .delayCount(0)
                    .remind(false)
                    .finished(false)
                    .build();
        }

        task.setContent(qo.getContent());
        task.setEndTime(qo.getEndTime());
        task.setStartTime(qo.getStartTime());
        task.setTitle(qo.getTitle());
        task.setRoutine(qo.isRoutine());
        task.setRoutineCron(qo.getRoutineCron());
        task.setTopicId(qo.getTopicId());
        taskDao.save(task);
        return true;
    }

    @Override
    public TaskVO info(Long currentUserId, Long taskId) {
        Task condition = Task.builder().userId(currentUserId).build();
        condition.setId(taskId);
        List<Task> tasks = taskDao.condition(condition);
        if(CollUtil.isEmpty(tasks)){
            return null;
        }
        TaskVO taskVO = BeanUtil.copyProperties(tasks.get(0), TaskVO.class);
        if(taskVO.getTopicId() == null){
            return taskVO;
        }
        List<TopicVO> topicTitles = topicService.getTitleByIds(ListUtil.of(taskVO.getTopicId()));
        if(CollUtil.isNotEmpty(topicTitles)){
            taskVO.setTopicFulTitle(topicTitles.get(0).getFullTitle());
        }

        return taskVO;
    }

    @Override
    public boolean remove(Long taskId) {
        Task condition = Task.builder().build();
        condition.setId(taskId);
        taskDao.remove(condition);
        return true;
    }

    @Override
    public void scanAndRemind() {
        log.info("To starting scan per remind tasks...");
        // from now to now + half an hour
        LocalDateTime fromStartTime = LocalDateTimeUtil.now();
        LocalDateTime toStartTime = LocalDateTimeUtil.offset(fromStartTime, 30, ChronoUnit.MINUTES);


        List<Task> remindTasks = taskDao.condition(Task.builder()
                .remind(false)
                .finished(false)
                .fromStartTime(fromStartTime)
                .toStartTime(toStartTime)
                .build());
        if(CollUtil.isEmpty(remindTasks)){
            log.warn("No per remind tasks...");
            return;
        }

        for (Task remindTask : remindTasks) {
            String content = remindTask.getTitle();
            String receiptId = null;
            UserVO userVO = userService.conditionOne(UserQO.builder().id(remindTask.getUserId()).build());
            if(userVO != null){
                receiptId = userVO.getPushPlusId();
            }

            boolean pushed = pushService.push(content, receiptId);
            if(pushed){
                remindTask.setRemind(true);
                taskDao.save(remindTask);
            }

        }
    }

    @Override
    public boolean delay(Long taskId, Long currentUserId) {
        Task task = taskDao.getById(taskId);
        Assert.isTrue(!task.getFinished(),()->new ServiceException("The task is finish"));

        // 每次推迟 +1 小时
        if(task.getDelayCount() == null){
            task.setDelayCount(0);
        }
        task.setDelayCount(task.getDelayCount()+1);
        task.setEndTime(LocalDateTimeUtil.offset(task.getEndTime(),task.getDelayCount(), ChronoUnit.HOURS));
        task.setRemind(false);
        taskDao.save(task);

        return false;
    }

    @Override
    public boolean finish(Long taskId, Long currentUserId) {
        Task task = taskDao.getById(taskId);
        Assert.isTrue(!task.getFinished(),()->new ServiceException("The task is finish"));

        try{
            // routine task
            if(task.getRoutine() && StrUtil.isNotBlank(task.getRoutineCron())){
                CronExpression expression = CronExpression.parse(task.getRoutineCron());
                LocalDateTime next = expression.next(LocalDateTime.now());
                if(next == null){
                    throw new ServiceException("The CRON expression fail");
                }
                Duration between = LocalDateTimeUtil.between(next,LocalDateTime.now());
                long betweenHour = between.toHours();

                // Set net time
                task.setStartTime(next);
                task.setEndTime(LocalDateTimeUtil.offset(task.getEndTime(),betweenHour,ChronoUnit.HOURS));
                // only remind once in one day
                task.setRemind(betweenHour >= 8);

            }else{
                task.setActualCloseTime(LocalDateTime.now());
                task.setFinished(true);
            }

            taskDao.save(task);
        }catch (Exception e){
            log.error("FINISH TASK ERROR",e);
            throw new ServiceException("unknown error");
        }

        return true;
    }
}

package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
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
import com.zion.learning.common.RemindType;
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
import java.time.format.DateTimeFormatter;
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

        condition.sort("taskTime", Sort.Direction.DESC);
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
                    if(LocalDateTime.now().isAfter(task.getTaskTime())){
                        vo.setExpireTag(this.calculateExpire(task.getTaskTime(),LocalDateTime.now(),"Expired "));
                    }else{
                        vo.setExpireTag(this.calculateExpire(LocalDateTime.now(),task.getTaskTime(),"Begin With "));
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
        task.setRemindTimeType(qo.getRemindTimeType());
        task.setRemindTimeNum(qo.getRemindTimeNum());
        task.setContent(qo.getContent());
        task.setTaskTime(qo.getTaskTime());
        task.setTitle(qo.getTitle());
        task.setRoutine(qo.isRoutine());
        task.setRoutineCron(qo.getRoutineCron());
        task.setTopicId(qo.getTopicId());
        // calculate remind time
        task.setRemindTime(this.calcRemindTime(task.getRemindTimeType(),task.getRemindTimeNum(),task.getTaskTime()));
        taskDao.save(task);
        return true;
    }

    private LocalDateTime calcRemindTime(Integer remindTimeType, Integer remindTimeNum, LocalDateTime taskTime) {
        RemindType remindType = RemindType.getType(remindTimeType);
        remindTimeNum = remindTimeNum == null ? 30 : remindTimeNum;
        return LocalDateTimeUtil.offset(taskTime, -remindTimeNum,remindType.getChronoUnit());
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

    private final static String CONTENT_FORMAT_TEMPLATE = "<p><strong>任务时间：</strong><span style=\"color: rgb(192, 80, 77);\">${taskTime}</span></p>" +
            "<p><strong>任务标题：</strong>${title}</p>" +
            "<p><strong>所属主题：</strong><span style=\"color: rgb(31, 73, 125);\"><em>${topicFulName}</em></span></p>" +
            "<p><strong>任务内容：</strong></p><table><tbody><tr class=\"firstRow\"><td width=\"811\" valign=\"top\" style=\"word-break: break-all; border-width: 1px; border-style: solid;\">${content}</td></tr></tbody></table><p><br/></p><p><br/></p><p><br/></p>";

    @Override
    public void scanAndRemind() {
        log.info("To starting scan per remind tasks...");
        // from now to now + half an hour
        LocalDateTime fromStartTime = LocalDateTimeUtil.now();
        LocalDateTime toStartTime = LocalDateTimeUtil.offset(fromStartTime, 30, ChronoUnit.MINUTES);


        List<Task> remindTasks = taskDao.condition(Task.builder()
                .remind(false)
                .finished(false)
                .fromTaskTime(fromStartTime)
                .toTaskTime(toStartTime)
                .build());
        if(CollUtil.isEmpty(remindTasks)){
            log.warn("No per remind tasks...");
            return;
        }

        Map<Long, String> topicTitleMap = new HashMap<>();
        List<TopicVO> topicVOS = topicService.getTitleByIds(remindTasks.stream().map(Task::getTopicId).collect(Collectors.toList()));
        if(CollectionUtil.isNotEmpty(topicVOS)){
            topicTitleMap = topicVOS.stream().collect(Collectors.toMap(TopicVO::getId, TopicVO::getFullTitle, (t1, t2) -> t1));
        }

        for (Task remindTask : remindTasks) {


            String content = CONTENT_FORMAT_TEMPLATE.replace("${taskTime}",remindTask.getTaskTime() != null?LocalDateTimeUtil.format(remindTask.getTaskTime(), "yyyy-MM-dd HH:mm:ss"):"-")
                    .replace("${title}",StrUtil.isNotBlank(remindTask.getTitle())?remindTask.getTitle():"-")
                    .replace("${content}",StrUtil.isNotBlank(remindTask.getContent())?remindTask.getContent():"-")
                    .replace("${topicFulName}", topicTitleMap.getOrDefault(remindTask.getTopicId(), "-"))
                    ;

            String receiptId = null;
            UserVO userVO = userService.conditionOne(UserQO.builder().id(remindTask.getUserId()).build());
            if(userVO != null){
                receiptId = userVO.getPushPlusId();
            }
            boolean pushed = pushService.push(remindTask.getTitle(),content, receiptId);
            if(pushed){
                remindTask.setRemind(true);
                taskDao.save(remindTask);
            }
            log.info("{}",content);

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
        task.setTaskTime(LocalDateTimeUtil.offset(task.getTaskTime(),task.getDelayCount(), ChronoUnit.HOURS));
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
                long betweenHour = between.toSeconds();

                // Set net time
                task.setTaskTime(next);
                task.setRemindTime(this.calcRemindTime(task.getRemindTimeType(),task.getRemindTimeNum(),task.getTaskTime()));

                // only remind once in one day
                if(task.getRemind()){
                    task.setRemind(betweenHour/(60*60) < 8);
                }

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

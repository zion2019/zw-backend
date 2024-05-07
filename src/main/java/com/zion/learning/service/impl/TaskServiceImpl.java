package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.pattern.CronPatternUtil;
import com.zion.common.basic.ExpireTagTypeEnum;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.vo.learning.request.TaskDelayQO;
import com.zion.common.vo.learning.request.TaskFinishQO;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTaskQO;
import com.zion.common.vo.learning.response.TaskExpireTagVo;
import com.zion.common.vo.learning.response.TaskVO;
import com.zion.common.vo.learning.response.TodoTaskVO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.common.vo.resource.request.UserQO;
import com.zion.common.vo.resource.response.UserVO;
import com.zion.learning.common.TaskStatus;
import com.zion.learning.common.TaskTimeType;
import com.zion.learning.common.TaskOperationType;
import com.zion.learning.dao.TaskDao;
import com.zion.learning.dao.TaskOperationDao;
import com.zion.learning.model.Task;
import com.zion.learning.model.TaskOperation;
import com.zion.learning.service.PushService;
import com.zion.learning.service.TaskService;
import com.zion.learning.service.TopicService;
import com.zion.resource.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    @Resource
    private TaskOperationDao taskOperationDao;

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
        if(qo.getTopicId() != null){
            condition.setTopicId(qo.getTopicId());
        }
        condition.sort("taskTime", Sort.Direction.ASC);
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

        vo.setTagType("Expired ".equals(expireType)?ExpireTagTypeEnum.danger:vo.getTagType());
        return vo;
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean addOrEditTask(Long userId,TaskQO qo){
        Task task ;
        TaskOperationType taskOperationType ;
        if(qo.getId() != null){
            task = taskDao.getById(qo.getId());
            taskOperationType = TaskOperationType.EDIT;
        }else{
            task = Task.builder()
                    .userId(userId)
                    .delayCount(0)
                    .finished(false)
                    .build();
            taskOperationType = TaskOperationType.NEW;
        }
        task.setRemindTimeType(qo.getRemindTimeType());
        task.setRemindTimeNum(qo.getRemindTimeNum());
        task.setContent(qo.getContent());
        task.setTaskTime(qo.getTaskTime());
        task.setTitle(qo.getTitle());
        task.setRoutine(qo.isRoutine());
        task.setStatus(TaskStatus.READING.getCode());
        task.setRoutineCron(qo.getRoutineCron());
        task.setTopicId(qo.getTopicId());
        this.calcRemindTime(task);
        taskDao.save(task);
        // save operation
        this.saveOperation(task.getId(),userId, taskOperationType,null);

        return true;
    }

    /**
     * save　task operation
     * @param taskId
     * @param userId
     * @param operationType
     * @param remark
     */
    private void saveOperation(Long taskId,Long userId, TaskOperationType operationType,String remark) {
        taskOperationDao.save(TaskOperation.builder()
                .taskId(taskId)
                .remark(remark)
                .userId(userId)
                .type(operationType.getCode())
                .build());
    }

    private LocalDateTime calcTaskTime(Integer taskTimeTypeCode, Integer taskTimeNum, LocalDateTime baseTime) {
        TaskTimeType taskTimeType = TaskTimeType.getType(taskTimeTypeCode);
        taskTimeNum = taskTimeNum == null ? 30 : taskTimeNum;
        return LocalDateTimeUtil.offset(baseTime, -taskTimeNum, taskTimeType.getChronoUnit());
    }

    @Override
    public TaskVO info(Long currentUserId, Long taskId) {
        TaskVO taskVO = new TaskVO();
        Task condition = Task.builder().userId(currentUserId).build();
        condition.setId(taskId);
        List<Task> tasks = taskDao.condition(condition);
        if(CollUtil.isEmpty(tasks)){
            return taskVO;
        }
        taskVO = BeanUtil.copyProperties(tasks.get(0), TaskVO.class);
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
        LocalDateTime toStartTime = LocalDateTimeUtil.offset(fromStartTime, 5, ChronoUnit.MINUTES);

        List<Task> remindTasks = taskDao.condition(Task.builder()
                .remind(false)
                .finished(false)
                .fromRemindTime(fromStartTime)
                .toRemindTime(toStartTime)
                .build());
        if(CollUtil.isEmpty(remindTasks)){
            log.debug("No per remind tasks...");
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
    public void autoFinish() {

        // The scan is based on tasks that have not been completed within 5 minutes of the current hour
        LocalDateTime fromTaskTime = LocalDateTimeUtil.offset(LocalDateTime.now(), -10, ChronoUnit.MINUTES);
        log.info("The scanning range from: {}",LocalDateTimeUtil.formatNormal(fromTaskTime));

        List<Task> tasks = taskDao.condition(Task.builder()
                .finished(false)
                .ltTaskTime(fromTaskTime)
                .build());
        if(CollUtil.isEmpty(tasks)){
            log.debug("No un finish tasks...");
            return;
        }
        for (Task task : tasks) {
            TaskFinishQO taskFinishQO = new TaskFinishQO();
            taskFinishQO.setTaskId(task.getId());
            taskFinishQO.setUserId(1L);
            taskFinishQO.setFinishRemark("auto finish");
            taskFinishQO.setTaskStatus(TaskStatus.CLOSED);
            this.finish(taskFinishQO);
        }

    }

    @Override
    public boolean delay(TaskDelayQO delayQO) {

        Task task = taskDao.getById(delayQO.getTaskId());
        Assert.isTrue(!task.getFinished(),()->new ServiceException("The task is finish"));
        task.setTaskTime(calcTaskTime(delayQO.getDelayTimeType(),delayQO.getDelayTimeNum(),LocalDateTimeUtil.now()));
        task.setDelayCount(task.getDelayCount() == null ? 1 :task.getDelayCount()+1);
        task.setTaskTime(LocalDateTimeUtil.offset(task.getTaskTime(),task.getDelayCount(), ChronoUnit.HOURS));
        this.calcRemindTime(task);
        taskDao.save(task);
        // save operation
        this.saveOperation(task.getId(),delayQO.getUserId(), TaskOperationType.DELAY,delayQO.getDelayReason());

        return false;
    }

    private void calcRemindTime(Task task) {
        // 1. base task time to calc remind time
        LocalDateTime remindTime = calcTaskTime(task.getRemindTimeType(), task.getRemindTimeNum(), task.getRemindTime());
        task.setRemindTime(remindTime);

        // 2. remind if  more than three hours
        if(LocalDateTimeUtil.between(remindTime,LocalDateTime.now()).toHours() > 3){
            task.setRemind(false);
        }
    }

    @Override
    public boolean finish(TaskFinishQO qo) {
        Task task = taskDao.getById(qo.getTaskId());
        Assert.isTrue(!task.getFinished(),()->new ServiceException("The task is finish"));

        try{
            LocalDateTime next = null;
            // routine task
            if(task.getRoutine() && StrUtil.isNotBlank(task.getRoutineCron())){
                CronExpression expression = CronExpression.parse(task.getRoutineCron());
                LocalDateTime baseTime = task.getTaskTime().isAfter(LocalDateTime.now()) ? task.getTaskTime() : LocalDateTime.now();
                try{

                    next = expression.next(baseTime);
                }catch (Exception e){
                    log.error("Parse CRON expression fail");
                }
                if(next == null){
                    log.warn("There is no next time ,cron:{}",task.getRoutineCron());
                }else{
                    // Daily tasks that exceed one 90 days are meaningless
                    if(LocalDateTimeUtil.between(baseTime,next).toDays() > 90){
                        log.warn("Daily tasks that exceed one hundred days are meaningless ,cron:{}",task.getRoutineCron());
                        next = null;
                    }
                }
            }

            if(next != null){
                // Set net time
                task.setTaskTime(next);
                this.calcRemindTime(task);
            }else{
                task.setStatus(qo.getTaskStatus().getCode());
                task.setActualCloseTime(LocalDateTime.now());
                task.setFinished(true);
            }

            taskDao.save(task);
            this.saveOperation(task.getId(),qo.getUserId(),TaskOperationType.FINISH,qo.getFinishRemark());
        }catch (Exception e){
            log.error("FINISH TASK ERROR",e);
            throw new ServiceException("unknown error");
        }

        return true;
    }
}

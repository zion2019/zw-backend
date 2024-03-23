package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.zion.common.basic.ExpireTagTypeEnum;
import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTaskQO;
import com.zion.common.vo.learning.response.TaskExpireTagVo;
import com.zion.common.vo.learning.response.TaskVO;
import com.zion.common.vo.learning.response.TodoTaskVO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.dao.TaskDao;
import com.zion.learning.model.Task;
import com.zion.learning.service.TaskService;
import com.zion.learning.service.TopicService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private TopicService topicService;

    @Override
    public Page<TodoTaskVO> page(TodoTaskQO qo) {
        Page<TodoTaskVO> voPage = new Page<>();
        voPage.setPageNo(qo.getPageNo());
        voPage.setPageSize(qo.getPageSize());
        Task condition = Task.builder().userId(qo.getUserId())
                .finished(false)
                .build();
        // limited today task
        if(qo.isToday()){
            condition.setGtEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
        }
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
                    if(LocalDateTime.now().compareTo(task.getStartTime()) > 0){
                        vo.setExpireTag(this.calculateExpire(task.getStartTime(),LocalDateTime.now(),"Begin With "));
                    }else{
                        if(LocalDateTime.now().compareTo(task.getEndTime()) > 0){
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
            vo.setTagType(ExpireTagTypeEnum.DANGER);
            vo.setTagName(expireType+"1 minute");
        } else if (remainingTime.getSeconds() < 3600) {
            vo.setTagType(ExpireTagTypeEnum.DANGER);
            vo.setTagName(expireType+remainingTime.toMinutes()+" minute");
        } else if (remainingTime.getSeconds() < 86400) {
            vo.setTagType(ExpireTagTypeEnum.WARNING);
            vo.setTagName(expireType+remainingTime.toHours()+" hours");
        } else if (remainingTime.getSeconds() < 2592000) { // 大约一个月的秒数
            vo.setTagType(ExpireTagTypeEnum.WARNING);
            vo.setTagName(expireType+remainingTime.toDays()+" days");
        } else if (remainingTime.getSeconds() < 31536000) { // 大约一年的秒数
            vo.setTagType(ExpireTagTypeEnum.WARNING);
            vo.setTagName(expireType+(int)(remainingTime.toDays() / 30)+" month");
        } else {
            vo.setTagType(ExpireTagTypeEnum.WARNING);
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

        return BeanUtil.copyProperties(tasks.get(0),TaskVO.class);
    }

    @Override
    public boolean remove(Long taskId) {
        Task condition = Task.builder().build();
        condition.setId(taskId);
        taskDao.remove(condition);
        return true;
    }

    public boolean removeTask(Long taskId){
        Task removeCondition = Task.builder().build();
        removeCondition.setId(taskId);
        taskDao.delete(removeCondition);
        return true;
    }
}

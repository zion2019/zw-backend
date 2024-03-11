package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTaskQO;
import com.zion.common.vo.learning.request.TopicQO;
import com.zion.common.vo.learning.response.TodoTaskVO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.dao.TaskDao;
import com.zion.learning.model.Task;
import com.zion.learning.model.Topic;
import com.zion.learning.service.TaskService;
import com.zion.learning.service.TopicService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    @Resource
    private TopicService topicService;

    @Override
    public Page<TodoTaskVO> todoList(TodoTaskQO qo) {
        Page<TodoTaskVO> voPage = new Page<>();
        voPage.setPageNo(qo.getPageNo());
        voPage.setPageSize(qo.getPageSize());
        Task condition = Task.builder().userId(qo.getUserId())
                .finished(false)
                .build();
        condition.sort("endTime", Sort.Direction.DESC);
        Page<Task> page = taskDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize())
                , Task.class, condition);

        List<TodoTaskVO> voList = new ArrayList<>(page.getPageSize());
        if(CollUtil.isNotEmpty(page.getDataList())){
            Map<Long, String> topicMap = new HashMap<>();
            List<Long> topicIdSet = page.getDataList().stream().map(Task::getTopicId).collect(Collectors.toList());
            List<TopicVO> topicInfos = topicService.getTitleByIds(topicIdSet);
            if(CollUtil.isNotEmpty(topicInfos)){
                topicMap = topicInfos.stream().collect(Collectors.toMap(TopicVO::getId, TopicVO::getFullTitle, (t1, t2) -> t1));
            }

            for (Task task : page.getDataList()) {
                TodoTaskVO vo = new TodoTaskVO();
                voList.add(vo);
                vo.setTaskId(task.getId());
                vo.setTitle(task.getTitle());
                // topic info
                vo.setTopicFullName(topicMap.get(task.getTopicId()));

                // calculate the remaining hour
                vo.setRemainingHour(LocalDateTimeUtil.between(task.getEndTime(),LocalDateTime.now(), ChronoUnit.HOURS));
            }


            voPage.setDataList(voList);
        }

        return voPage;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean addOrEditTask(Long userId,List<TaskQO> qos){
        for (TaskQO qo : qos) {
            Task task ;
            if(qo.getId() != null){
                task = taskDao.getById(qo.getId());
            }else{
                task = Task.builder()
                        .userId(userId)
                        .finished(false)
                        .build();
            }

            task.setDescription(qo.getDescription());
            task.setEndTime(qo.getEndTime());
            task.setStartTime(qo.getStartTime());
            task.setTitle(qo.getTitle());
            task.setTopicId(qo.getTopicId());
            taskDao.save(task);
        }
        return true;
    }

    public boolean removeTask(Long taskId){
        Task removeCondition = Task.builder().build();
        removeCondition.setId(taskId);
        taskDao.delete(removeCondition);
        return true;
    }
}

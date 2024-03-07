package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTodayQO;
import com.zion.common.vo.learning.response.TodoTodayVO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.dao.TaskDao;
import com.zion.learning.model.Task;
import com.zion.learning.service.TaskService;
import com.zion.learning.service.TopicService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    @Resource
    private TopicService topicService;

    @Override
    public List<TodoTodayVO> todoToday(TodoTodayQO qo) {
        List<TodoTodayVO> todayVos = new ArrayList<>();
        Task condition = Task.builder().userId(qo.getUserId())
                .finished(false)
                .build();
        condition.sort("endTime", Sort.Direction.DESC);
        Page<Task> page = taskDao.pageQuery(new Page<Task>(0, 2)
                , Task.class, condition);

        if(CollUtil.isNotEmpty(page.getDataList())){
            for (Task task : page.getDataList()) {
                TodoTodayVO vo = new TodoTodayVO();
                todayVos.add(vo);
                vo.setTaskId(task.getId());
                vo.setTitle(task.getTitle());
                // topic info
                TopicVO topicVO = topicService.info(task.getTopicId(), qo.getUserId());
                vo.setTopicFullName(topicVO.getFullTitle());

                // calculate the remaining hour
                vo.setRemainingHour(LocalDateTimeUtil.between(task.getEndTime(),LocalDateTime.now(), ChronoUnit.HOURS));
            }
            todayVos = BeanUtil.copyToList(page.getDataList(),TodoTodayVO.class);
        }

        return todayVos;
    }


    public boolean addOrEditTask(Long userId,List<TaskQO> qos){
        for (TaskQO qo : qos) {
            if(qo.getId() != null){
                Task removeCondition = Task.builder().build();
                removeCondition.setId(qo.getId());
                taskDao.delete(removeCondition);
            }

            Task task = BeanUtil.copyProperties(qo, Task.class);
            task.setUserId(userId);
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

package com.zion.learning.service;

import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTodayQO;
import com.zion.common.vo.learning.response.TodoTodayVO;

import java.util.List;

public interface TaskService {
    List<TodoTodayVO> todoToday(TodoTodayQO qo);

    boolean addOrEditTask(Long userId,List<TaskQO> qos);
}

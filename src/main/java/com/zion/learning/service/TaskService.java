package com.zion.learning.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTaskQO;
import com.zion.common.vo.learning.response.TodoTaskVO;
import com.zion.common.vo.learning.response.TaskVO;

import java.util.List;

public interface TaskService {
    Page<TodoTaskVO> todoList(TodoTaskQO qo);

    boolean addOrEditTask(Long userId,TaskQO qo);

    TaskVO info(Long currentUserId, Long taskId);
}

package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTodayQO;
import com.zion.common.vo.learning.response.TodoTodayVO;
import com.zion.learning.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController("/task")
public class TaskController extends BaseController {
    private TaskService taskService;

    @PostMapping("/todo")
    public R<TodoTodayVO> todoToday(@RequestBody TodoTodayQO qo){
        return R.ok(taskService.todoToday(qo));
    }

    @PostMapping
    public R<Boolean> addTask(@RequestBody List<TaskQO> qos){
        return R.ok(taskService.addOrEditTask(getCurrentUserId(),qos));
    }


}

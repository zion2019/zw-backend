package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTaskQO;
import com.zion.common.vo.learning.response.TodoTaskVO;
import com.zion.learning.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/learn/task")
public class TaskController extends BaseController {
    @Resource
    private TaskService taskService;

    @PostMapping
    public R<Boolean> addOrEditTask(@RequestBody List<TaskQO> qos){
        return R.ok(taskService.addOrEditTask(getCurrentUserId(),qos));
    }

    @PostMapping("/todo")
    public R<TodoTaskVO> todo(@RequestBody TodoTaskQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(taskService.todoList(qo));
    }


}

package com.zion.learning.controller;

import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.TodoTodayQO;
import com.zion.learning.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController("/task")
public class TaskController {
    private TaskService taskService;


    @PostMapping("/todo")
    public R todoToday(@RequestBody TodoTodayQO qo){
        taskService.todoToday(qo);
        return R.ok();
    }


}

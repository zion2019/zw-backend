package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.TaskQO;
import com.zion.common.vo.learning.request.TodoTaskQO;
import com.zion.common.vo.learning.response.TodoTaskVO;
import com.zion.common.vo.learning.response.TaskVO;
import com.zion.learning.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/task")
public class TaskController extends BaseController {
    @Resource
    private TaskService taskService;

    @PostMapping
    public R<Boolean> addOrEditTask(@RequestBody TaskQO qo){
        return R.ok(taskService.addOrEditTask(getCurrentUserId(),qo));
    }

    @GetMapping("/info/{taskId}")
    public R<TaskVO> info(@PathVariable("taskId")Long taskId){
        return R.ok(taskService.info(getCurrentUserId(),taskId));
    }


    @PostMapping("/page")
    public R<TodoTaskVO> page(@RequestBody TodoTaskQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(taskService.page(qo));
    }

    @DeleteMapping
    public R<Boolean> remove(@RequestParam("taskId")Long taskId){
        return R.ok(taskService.remove(taskId));
    }

    @GetMapping("/test")
    public void test(){
        taskService.scanAndRemind();
    }
}

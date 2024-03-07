package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.TopicQO;
import com.zion.learning.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/learn/topic")
public class TopicController extends BaseController {

    private TopicService topicService;

    @GetMapping
    public R list(TopicQO qo){
        return R.ok(topicService.list(qo));
    }

    @GetMapping("/tree")
    public R tree(@RequestParam(value = "excludeId",required = false) Long excludeId){
        return R.ok(topicService.tree(getCurrentUserId(),excludeId,false));
    }

    @PostMapping
    public R save(@RequestBody TopicQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(topicService.save(qo));
    }

    @GetMapping("/info")
    public R info(@RequestParam("id")Long id){
        return R.ok(topicService.info(id,getCurrentUserId()));
    }

    @DeleteMapping
    public R delete(@RequestParam("topicId")Long topicId){
        return R.ok(topicService.delete(topicId));
    }

}

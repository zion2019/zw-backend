package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.LearningStrategyIntervalQO;
import com.zion.common.vo.learning.response.LearningStrategyIntervalVO;
import com.zion.learning.service.LearningStrategyIntervalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/strategy/interval")
public class LearningStrategyIntervalController extends BaseController {
    
    @Resource
    private LearningStrategyIntervalService learningStrategyIntervalService;
    
    @PostMapping
    public R<Boolean> save(@RequestBody LearningStrategyIntervalQO qo) {
        return R.ok(learningStrategyIntervalService.save(qo));
    }
    
    @GetMapping("/{id}")
    public R<LearningStrategyIntervalVO> info(@PathVariable("id") Long id) {
        return R.ok(learningStrategyIntervalService.info(id));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(learningStrategyIntervalService.delete(id));
    }
}
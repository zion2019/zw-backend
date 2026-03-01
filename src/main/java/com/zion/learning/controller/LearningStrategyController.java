package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.LearningStrategyQO;
import com.zion.common.vo.learning.response.LearningStrategyVO;
import com.zion.learning.service.LearningStrategyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/strategy")
public class LearningStrategyController extends BaseController {
    
    @Resource
    private LearningStrategyService learningStrategyService;
    
    @PostMapping
    public R<Boolean> save(@RequestBody LearningStrategyQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(learningStrategyService.save(qo));
    }
    
    @GetMapping("/{id}")
    public R<LearningStrategyVO> info(@PathVariable("id") Long id) {
        return R.ok(learningStrategyService.info(id, getCurrentUserId()));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(learningStrategyService.delete(id));
    }
    
    @PostMapping("/default/{id}")
    public R<Boolean> setDefault(@PathVariable("id") Long id) {
        return R.ok(learningStrategyService.setDefaultStrategy(id, getCurrentUserId()));
    }

    
    @GetMapping("/page")
    public R page(LearningStrategyQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(learningStrategyService.page(qo));
    }
    
    @GetMapping
    public R list(LearningStrategyQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(learningStrategyService.list(qo));
    }
}
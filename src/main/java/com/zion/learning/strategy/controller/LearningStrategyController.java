package com.zion.learning.strategy.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.LearningStrategyQO;
import com.zion.common.vo.learning.response.LearningStrategyVO;
import com.zion.learning.strategy.service.LearningStrategyService;
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
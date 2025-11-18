package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.learning.service.StageService;
import com.zion.common.vo.learning.request.StageQO;
import com.zion.common.vo.learning.response.StageVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/stage")
public class StageController extends BaseController {
    
    @Resource
    private StageService stageService;
    
    @PostMapping
    public R<Boolean> save(@RequestBody StageQO qo) {
        return R.ok(stageService.save(qo));
    }
    
    @GetMapping("/{id}")
    public R<StageVO> info(@PathVariable("id") Long id) {
        return R.ok(stageService.info(id, getCurrentUserId()));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(stageService.delete(id));
    }
    
    @GetMapping("/page")
    public R page(StageQO qo) {
        return R.ok(stageService.page(qo));
    }
    
    @GetMapping
    public R list(StageQO condition) {
        return R.ok(stageService.list(condition));
    }
}
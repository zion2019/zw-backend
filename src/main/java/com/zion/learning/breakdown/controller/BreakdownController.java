package com.zion.learning.breakdown.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.BreakdownQO;
import com.zion.common.vo.learning.response.BreakdownVO;
import com.zion.learning.breakdown.service.BreakdownService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/breakdown")
public class BreakdownController extends BaseController {
    
    @Resource
    private BreakdownService breakdownService;
    
    @PostMapping
    public R<Boolean> save(@RequestBody BreakdownQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(breakdownService.save(qo));
    }
    
    @GetMapping("/{id}")
    public R<BreakdownVO> info(@PathVariable("id") Long id) {
        return R.ok(breakdownService.info(id, getCurrentUserId()));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(breakdownService.delete(id));
    }
    
    @GetMapping("/page")
    public R page(BreakdownQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(breakdownService.page(qo));
    }
    
    @GetMapping
    public R list(BreakdownQO qo) {
        return R.ok(breakdownService.list(qo));
    }
}
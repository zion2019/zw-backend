package com.zion.learning.practice.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.learning.practice.model.PracticeRecord;
import com.zion.learning.practice.service.PracticeService;
import com.zion.common.vo.learning.request.PracticeRecordQO;
import com.zion.common.vo.learning.response.PracticeRecordVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/practice")
public class PracticeController extends BaseController {
    
    @Resource
    private PracticeService practiceService;
    
    @PostMapping
    public R<Boolean> practice(@RequestBody PracticeRecordQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(practiceService.practice(qo));
    }
    
    @GetMapping("/{id}")
    public R<PracticeRecordVO> info(@PathVariable("id") Long id) {
        return R.ok(practiceService.info(id, getCurrentUserId()));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(practiceService.delete(id));
    }
    

    
    @GetMapping("/page")
    public R page(PracticeRecordQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(practiceService.page(qo));
    }
    
    @GetMapping
    public R list(PracticeRecord condition) {
        condition.setUserId(getCurrentUserId());
        return R.ok(practiceService.list(condition));
    }
}
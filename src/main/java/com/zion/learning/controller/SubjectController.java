package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.SubjectQO;
import com.zion.common.vo.learning.response.SubjectVO;
import com.zion.learning.service.SubjectService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/subject")
public class SubjectController extends BaseController {
    
    @Resource
    private SubjectService subjectService;

    @GetMapping
    public R<SubjectVO> todayReview() {
        return R.ok(subjectService.getTodayReviewList(getCurrentUserId()));
    }
    
    @PostMapping
    public R<Boolean> save(@RequestBody SubjectQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(subjectService.save(qo));
    }
    
    @GetMapping("/{id}")
    public R<SubjectVO> info(@PathVariable("id") Long id) {
        return R.ok(subjectService.info(id, getCurrentUserId()));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(subjectService.delete(id));
    }
    
    @GetMapping("/page")
    public R page(SubjectQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(subjectService.page(qo));
    }
}
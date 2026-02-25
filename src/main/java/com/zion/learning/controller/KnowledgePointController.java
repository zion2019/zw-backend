package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.learning.model.KnowledgePoint;
import com.zion.learning.service.KnowledgePointService;
import com.zion.common.vo.learning.request.KnowledgePointQO;
import com.zion.common.vo.learning.response.KnowledgePointVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/knowledge-point")
public class KnowledgePointController extends BaseController {
    
    @Resource
    private KnowledgePointService knowledgePointService;
    
    @PostMapping
    public R<Boolean> save(@RequestBody KnowledgePointQO qo) {
        return R.ok(knowledgePointService.save(qo));
    }
    
    @GetMapping("/{id}")
    public R<KnowledgePointVO> info(@PathVariable("id") Long id) {
        return R.ok(knowledgePointService.info(id, getCurrentUserId()));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(knowledgePointService.delete(id));
    }
    
    @GetMapping("/page")
    public R page(KnowledgePointQO qo) {
        return R.ok(knowledgePointService.page(qo));
    }
    
    @GetMapping
    public R list(KnowledgePointQO qo) {
        return R.ok(knowledgePointService.list(qo));
    }
}
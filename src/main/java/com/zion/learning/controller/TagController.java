package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.Page;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.TagQO;
import com.zion.common.vo.learning.response.TagVO;
import com.zion.learning.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/tag")
public class TagController extends BaseController {
    
    @Resource
    private TagService tagService;
    
    @PostMapping
    public R<Boolean> save(@RequestBody TagQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(tagService.save(qo));
    }
    
    @GetMapping("/{id}")
    public R<TagVO> info(@PathVariable("id") Long id) {
        return R.ok(tagService.info(id, getCurrentUserId()));
    }
    
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        return R.ok(tagService.delete(id));
    }
    
    @GetMapping("/tree")
    public R<TagVO> tree() {
        return R.ok(tagService.tree(getCurrentUserId()));
    }
    
    @GetMapping("/page")
    public R<Page<TagVO>> page(TagQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(tagService.page(qo));
    }
    
    @GetMapping
    public R<TagVO> list(TagQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(tagService.list(qo));
    }
}
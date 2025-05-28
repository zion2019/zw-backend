package com.zion.bill.controller;

import com.zion.bill.service.BillCategoryService;
import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.bill.req.CategoryQO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill/category")
public class BillCategoryController extends BaseController {


    @Resource
    private BillCategoryService billCategoryService;

    @GetMapping("/tree")
    public R tree(@RequestParam(value = "excludeId",required = false) Long excludeId){
        return R.ok(billCategoryService.tree(getCurrentUserId(),excludeId,false));
    }


    @PostMapping("/list")
    public R list(@RequestBody CategoryQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(billCategoryService.list(qo));
    }

    @PostMapping("/bills/page")
    public R billsPage(@RequestBody CategoryQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(billCategoryService.billsPage(qo));
    }


    @PostMapping
    public R save(@RequestBody CategoryQO qo){
        qo.setUserId(getCurrentUserId());
        billCategoryService.save(qo);
        return R.ok();
    }

    @DeleteMapping
    public R delete(@RequestParam("categoryId")Long categoryId){
        billCategoryService.delete(categoryId);
        return R.ok();
    }

    @GetMapping("/info")
    public R info(@RequestParam("id")Long id){
        return R.ok(billCategoryService.info(id,getCurrentUserId()));
    }


}



package com.zion.bill.controller;

import com.zion.bill.service.BillService;
import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.bill.req.BillChartQO;
import com.zion.common.vo.bill.req.BillQO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
public class BillController extends BaseController {

    @Resource
    private BillService billService;

    @PostMapping
    public R save(@RequestBody @Validated BillQO qo){
        qo.setUserId(getCurrentUserId());
        billService.save(qo);
        return R.ok();
    }

    @GetMapping
    public R list(@RequestParam("id") Long id){
        return R.ok(billService.info(id,getCurrentUserId()));
    }

    /**
     * 最近高频使用的标签
     * @param count 标签数量
     */
    @GetMapping("/category/recently")
    public R recentlyCategory(@RequestParam("count")Integer count){
        return R.ok(billService.recentlyCategory(count,getCurrentUserId()));
    }

    @GetMapping("/test")
    public R test(){
        billService.sendBillEmail();
        return R.ok();
    }


}

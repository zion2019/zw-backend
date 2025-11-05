package com.zion.bill.controller;

import com.zion.bill.service.BillChannelService;
import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.bill.req.ChannelQO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill/channel")
public class BillChannelController extends BaseController {

    @Resource
    private BillChannelService billChannelService;

    @PostMapping
    public R save(@RequestBody @Validated ChannelQO qo) {
        qo.setUserId(getCurrentUserId());
        billChannelService.save(qo);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable("id") Long id) {
        billChannelService.delete(id, getCurrentUserId());
        return R.ok();
    }

    @GetMapping("/{id}")
    public R info(@PathVariable("id") Long id) {
        return R.ok(billChannelService.info(id, getCurrentUserId()));
    }

    @GetMapping
    public R page(ChannelQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(billChannelService.page(qo));
    }

    @GetMapping("/list")
    public R list(ChannelQO qo) {
        qo.setUserId(getCurrentUserId());
        return R.ok(billChannelService.condition(qo));
    }
}
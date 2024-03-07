package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.PointQO;
import com.zion.learning.service.PointService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learn/point")
public class PointController extends BaseController {

    @Resource
    private PointService pointService;

    @GetMapping
    public R list(PointQO pointQO){
        return R.ok(pointService.list(pointQO));
    }

    @GetMapping("/info")
    public R info(@RequestParam("pointId")Long pointId){
        return R.ok(pointService.info(pointId));
    }

    @PostMapping
    public R save(@RequestBody PointQO pointQO){
        pointQO.setUserId(getCurrentUserId());
        return R.ok(pointService.save(pointQO));
    }

    @DeleteMapping
    public R delete(@RequestParam("pointId")Long pointId){
        return R.ok(pointService.delete(pointId));
    }

}

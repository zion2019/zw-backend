package com.zion.learning.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.request.PracticeQO;
import com.zion.learning.service.PracticeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/learn/practice")
public class PracticeController extends BaseController {

    private PracticeService practiceService;

    @GetMapping("/today")
    public R todayList(PracticeQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(practiceService.todayList(qo));
    }

    @PostMapping
    public R practise(@RequestBody PracticeQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(practiceService.practise(qo));
    }

    @GetMapping("/point")
    public R nextPoint(PracticeQO qo){
        qo.setUserId(getCurrentUserId());
        return R.ok(practiceService.nextPoint(qo));
    }
}

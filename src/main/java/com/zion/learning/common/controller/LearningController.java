package com.zion.learning.common.controller;

import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.vo.learning.response.LearnReviewTodayVO;
import com.zion.learning.common.service.LearningService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learn")
public class LearningController extends BaseController {

    @Resource
    private LearningService learningService;

    @GetMapping("/today/review")
    public R<LearnReviewTodayVO> getTodayReviewInfo() {
        return R.ok(learningService.getTodayReviewInfo(getCurrentUserId()));
    }


}

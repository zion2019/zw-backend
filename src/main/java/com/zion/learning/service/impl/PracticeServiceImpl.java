package com.zion.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.PointQO;
import com.zion.common.vo.learning.request.PracticeQO;
import com.zion.common.vo.learning.response.PointVo;
import com.zion.common.vo.learning.response.PractiseVO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.common.PractiseResult;
import com.zion.learning.dao.PracticeDao;
import com.zion.learning.model.Practice;
import com.zion.learning.service.PointService;
import com.zion.learning.service.PracticeService;
import com.zion.learning.service.TopicService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class PracticeServiceImpl implements PracticeService {

    private PracticeDao practiceDao;

    private TopicService topicService;

    private PointService pointService;

    @Override
    public Page todayList(PracticeQO qo) {


        // Get the not done, time greater than zero today, paging, reverse order, topicId
        Practice condition = Practice.builder().userId(qo.getUserId())
//                .result(PractiseResult.UNDO)
                .ltePractiseDate(LocalDateTimeUtil.endOfDay(LocalDateTime.now())).build();
        condition.setUserId(qo.getUserId());
        Page<PractiseVO> page =  practiceDao.findUndoTopicByDate(qo.getPageNo(),qo.getPageSize(),condition);
        if(CollUtil.isEmpty(page.getDataList())){
            return page;
        }

        List<Long> topics = page.getDataList().stream().map(PractiseVO::getTopicId).collect(Collectors.toList());

        // full title
        List<TopicVO> topicInfos = topicService.getTitleByIds(topics);
        if(CollUtil.isEmpty(topicInfos)){
            return page;
        }
        Map<Long, TopicVO> topicInfoMap = topicInfos.stream().collect(Collectors.toMap(TopicVO::getId, Function.identity(),(t1,t2)->t1));
        // undoCount,doneCount,completePercent
        condition.setResult(null);
        condition.include("topicId","result","practiseDate","updatedTime");
        condition.setTopicIds(topics);
        List<Practice> practices = practiceDao.condition(condition);
        if(CollUtil.isEmpty(practices)){
            return new Page();
        }

        Map<Long, List<Practice>> groupMap = practices.stream().collect(Collectors.groupingBy(Practice::getTopicId));
        for (PractiseVO vo : page.getDataList()) {

            TopicVO info = topicInfoMap.get(vo.getTopicId());
            vo.setTitle(info.getTitle());
            vo.setFullTitle(info.getFullTitle());
            vo.setBackground(info.getBackground());

            List<Practice> practisesForTopic = groupMap.get(vo.getTopicId());
            if(CollUtil.isEmpty(practisesForTopic)){
                continue;
            }

            vo.setUndoCount(BigDecimal.valueOf(practisesForTopic.stream().filter(p -> PractiseResult.UNDO.equals(p.getResult())).count()));
            vo.setToDayDoneCount(BigDecimal.ZERO);
            vo.setToDayCompletePercent(new BigDecimal("-1"));

            // today done
            practisesForTopic.stream().filter(p ->
                    p.getUpdatedTime() != null
                    && LocalDateTimeUtil.beginOfDay(LocalDateTime.now()).compareTo(LocalDateTimeUtil.beginOfDay(p.getUpdatedTime())) == 0
                    && PractiseResult.DONE.equals(p.getResult())
            ).forEach(p->vo.setToDayDoneCount(vo.getToDayDoneCount().add(BigDecimal.ONE)));

            // today total = today done + undo
            vo.setToDayTotalCount(vo.getToDayDoneCount().add(vo.getUndoCount()));
            if(vo.getToDayDoneCount() != null && vo.getToDayTotalCount() != null && vo.getToDayTotalCount().compareTo(BigDecimal.ZERO) > 0){
                vo.setToDayCompletePercent(vo.getToDayDoneCount().divide(vo.getToDayTotalCount(),2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            }
        }

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean practise(PracticeQO qo) {

        Assert.isTrue(qo.getId() != null,"the practise id is required!");
        Assert.isTrue(qo.getPointId() != null,"the point id is required!");
        Assert.isTrue(qo.getResult() != null,"the result id is required!");
        Assert.isTrue(!PractiseResult.UNDO.equals(qo.getResult()),"the result must in done and forget!");

        // Changing the result
        Practice practice = practiceDao.getById(qo.getId());
        Assert.isTrue(practice != null,"The practise recorder is not exist!");
        practice.setResult(qo.getResult());
        practiceDao.save(practice);

        // Reviewing and upgrading the point and get rest day
        pointService.upgrade(qo.getResult(), PointQO.builder().userId(qo.getUserId()).id(qo.getPointId()).build());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PractiseVO nextPoint(PracticeQO qo) {
        PractiseVO vo = new PractiseVO();
        Assert.isTrue(qo.getTopicId() != null,"the topic id is required!");

        Practice condition = Practice.builder().topicId(qo.getTopicId()).result(PractiseResult.UNDO).build();
        condition.setUserId(qo.getUserId());
        condition.setLtePractiseDate(LocalDateTimeUtil.endOfDay(LocalDateTimeUtil.now()));
        condition.sort("practiseTime", Sort.Direction.DESC);
        condition.include("id","topicId","pointId");
        Practice practice = practiceDao.conditionOne(condition);
        if(practice == null){
            return vo;
        }

        PointVo info = pointService.info(practice.getPointId());
        vo.setPoint(info);
        vo.setId(practice.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveNext(PracticeQO qo) {
        Assert.isTrue(qo.getUserId() != null,"The user id is required!");
        Assert.isTrue(qo.getTopicId() != null,"The topic id is required!");
        Assert.isTrue(qo.getPointId() != null,"The point id is required!");
        Assert.isTrue(qo.getIntervalDays() != null,"The interval days id is required!");

        // clear current undo
        Practice condition = Practice.builder().topicId(qo.getTopicId())
                .userId(qo.getUserId())
                .pointId(qo.getPointId())
                .result(PractiseResult.UNDO).build();
        practiceDao.remove(condition);

        // save
        condition.setPractiseDate(LocalDateTimeUtil.beginOfDay(LocalDateTimeUtil.offset(LocalDateTime.now(),qo.getIntervalDays(), ChronoUnit.DAYS)));
        practiceDao.save(condition);
        return true;
    }
}

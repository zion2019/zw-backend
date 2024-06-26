package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.learning.service.excel.PointExcelDto;
import com.zion.common.vo.learning.request.PointQO;
import com.zion.common.vo.learning.request.PracticeQO;
import com.zion.common.vo.learning.response.PointVo;
import com.zion.common.vo.learning.response.SubPointVo;
import com.zion.common.vo.learning.response.TopicStatisticVo;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.common.DegreeOfMastery;
import com.zion.learning.common.PractiseResult;
import com.zion.learning.dao.PointDao;
import com.zion.learning.dao.SubPointDao;
import com.zion.learning.model.Point;
import com.zion.learning.model.SubPoint;
import com.zion.learning.model.Topic;
import com.zion.learning.service.PointService;
import com.zion.learning.service.PracticeService;
import com.zion.learning.service.TopicService;
import com.zion.learning.service.excel.PointExcelImportListener;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class PointServiceImpl implements PointService{

    @Resource
    private PointDao pointDao;
    @Resource
    private SubPointDao subPointDao;
    @Resource
    private TopicService topicService;

    @Resource
    private PracticeService practiceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean upgrade(PractiseResult result, PointQO qo) {
        Point point = pointDao.getById(qo.getId());
        Assert.isTrue(point != null,"The point recorder is not exist!");
        DegreeOfMastery upgradeDegree =PractiseResult.DONE.equals(result)?DegreeOfMastery.upgrade(point.getDegreeOfMastery()):DegreeOfMastery.UNDERSTAND;
        // upgrade
        point.setDegreeOfMastery(upgradeDegree);
        pointDao.save(point);

        if(upgradeDegree != null){
           return practiceService.saveNext(PracticeQO.builder().topicId(point.getTopicId())
                    .pointId(point.getId())
                    .userId(qo.getUserId())
                    .intervalDays(upgradeDegree.getIntervalDay())
                    .build());
        }

        return true;
    }

    @Override
    public boolean existPointByTopicId(Long topicId) {
        return pointDao.conditionCount(Point.builder().topicId(topicId).build()) > 0;
    }

    /**
     * 统计主题下学习程度
     */
    public TopicStatisticVo statisticMastery(Long topicId) {
        TopicStatisticVo statisticVo = new TopicStatisticVo();
        List<Point> points = pointDao.condition(Point.builder().topicId(topicId).build());
        if(CollUtil.isEmpty(points)){
            return statisticVo;
        }

        statisticVo.setPointCount(points.size());
        // TODO Calculate present of mastery

        return statisticVo;
    }

    /**
     * list with page
     */
    public Page<PointVo> list(PointQO pointQO) {
        return pointDao.pageQuery(new Page<>(pointQO.getPageNo(),pointQO.getPageSize()),PointVo.class
                , Point.builder().title(pointQO.getTitle()).topicId(pointQO.getTopicId()).build());
    }

    /**
     * save or update
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PointQO pointQO) {
        Assert.isTrue(CharSequenceUtil.isNotBlank(pointQO.getTitle()),"The tile is required!");
        Assert.isTrue(pointQO.getTopicId() != null,"The topic id is required!");

        // count sub-point
        pointQO.setSubPointCount(CollUtil.isNotEmpty(pointQO.getSubPoints())?pointQO.getSubPoints().size():0);
        // init degree of mastery
        if(pointQO.getDegreeOfMastery() == null){
            pointQO.setDegreeOfMastery(DegreeOfMastery.UNDERSTAND);
        }
        // save point.
        Point point = pointDao.save(BeanUtil.copyProperties(pointQO, Point.class));

        // save sub-point delete and save
        if(CollUtil.isNotEmpty(pointQO.getSubPoints())){
            pointQO.getSubPoints().forEach(sub -> sub.setPointId(point.getId()));
            subPointDao.saveBatch(pointQO.getSubPoints());
        }

        // increase the weight of topic
        if(pointQO.getId() == null){
            topicService.increaseWeight(pointQO.getTopicId());
        }
        practiceService.saveNext(PracticeQO.builder().topicId(point.getTopicId())
                .pointId(point.getId())
                .userId(pointQO.getUserId())
                .intervalDays(DegreeOfMastery.UNDERSTAND.getIntervalDay())
                .build());

        return true;
    }

    public PointVo info(Long pointId) {
        PointVo vo = new PointVo();
        Point point = pointDao.getById(pointId);
        Assert.isTrue(point != null,"The Point is notfound");
        BeanUtil.copyProperties(point,vo);

        // sub-point
        List<SubPoint> subPoints = subPointDao.condition(SubPoint.builder().pointId(pointId).build());
        if(CollUtil.isNotEmpty(subPoints)){
            vo.setSubPoints(BeanUtil.copyToList(subPoints, SubPointVo.class));
        }

        return vo;
    }

    public boolean delete(Long pointId) {
        Point build = Point.builder().build();
        build.setId(pointId);
        pointDao.delete(build);
        subPointDao.delete(SubPoint.builder().pointId(pointId).build());
        practiceService.deleteByPointId(pointId);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PointExcelDto> importByExcel(MultipartFile file,Long userId) {
        PointExcelImportListener listener = new PointExcelImportListener(this,topicService);
        try{
            EasyExcel.read(file.getInputStream()
                    , PointExcelDto.class
                    , listener).doReadAll();
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }

        if(CollUtil.isNotEmpty(listener.getPerRemovePointId())){
            listener.getPerRemovePointId().forEach(this::delete);
        }

        if(CollUtil.isNotEmpty(listener.getInsertPoint())){
            listener.getInsertPoint().forEach(point -> {
                pointDao.save(point);
                practiceService.saveNext(PracticeQO.builder().topicId(point.getTopicId())
                        .pointId(point.getId())
                        .userId(userId)
                        .intervalDays(DegreeOfMastery.UNDERSTAND.getIntervalDay())
                        .build());
            });
        }

        if(CollUtil.isNotEmpty(listener.getInsertSubPoint())){
            listener.getInsertSubPoint().forEach(subPoint -> {
                subPoint.setPointId(subPoint.getPoint().getId());
                subPointDao.save(subPoint);
            });
        }

        return listener.getExcelErrorDataList();

    }

    @Override
    public List<Point> condition(Point pointCondition) {
        return pointDao.condition(pointCondition);
    }
}

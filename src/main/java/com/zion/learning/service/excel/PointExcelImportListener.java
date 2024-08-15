package com.zion.learning.service.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.common.DegreeOfMastery;
import com.zion.learning.model.Point;
import com.zion.learning.model.SubPoint;
import com.zion.learning.model.Topic;
import com.zion.learning.service.PointService;
import com.zion.learning.service.TopicService;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PointExcelImportListener implements ReadListener<PointExcelDto> {

    private final PointService pointService;
    private final TopicService topicService;

    /**
     * parse Excel data
     */
    private List<PointExcelDto> excelParseDataList;
    @Getter
    private List<PointExcelDto> excelErrorDataList;

    @Getter
    private Set<Long> perRemovePointId ;
    @Getter
    private List<Point> insertPoint ;
    @Getter
    private List<SubPoint> insertSubPoint ;

    public PointExcelImportListener(PointService pointService,TopicService topicService) {
        this.pointService = pointService;
        this.topicService = topicService;
    }


    private String lastTopic;
    private String lastTitle;

    @Override
    public void invoke(PointExcelDto pointExcelDto, AnalysisContext analysisContext) {
        if(CollUtil.isEmpty(excelParseDataList)){
            excelParseDataList = new ArrayList<>();
        }
        if(StrUtil.isNotBlank(pointExcelDto.getTopic())){
            lastTopic = pointExcelDto.getTopic();
        }else{
            pointExcelDto.setTopic(lastTopic);
        }
        if(StrUtil.isNotBlank(pointExcelDto.getTitle())){
            lastTitle = pointExcelDto.getTitle();
        }else{
            pointExcelDto.setTitle(lastTitle);
        }
        excelParseDataList.add(pointExcelDto);
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if(CollUtil.isEmpty(excelParseDataList)){
            return;
        }
        Map<String, Long> topicMap = new HashMap<>();
        List<TopicVO> topicVOS = topicService.condition(Topic.builder()
                .topicCodes(excelParseDataList.stream().map(PointExcelDto::getTopic).collect(Collectors.toSet()))
                .build());
        if(CollUtil.isNotEmpty(topicVOS)){
            topicMap = topicVOS.stream().collect(Collectors.toMap(TopicVO::getCode, TopicVO::getId, (t1, t2) -> t1));
        }

        Map<Long,Map<String, Point>> topicPNamePIDMap = new HashMap<>();
        Point pointCondition = Point.builder().titles(excelParseDataList.stream().map(PointExcelDto::getTitle).collect(Collectors.toSet())).build();
        if(!topicMap.isEmpty()){
            pointCondition.setTopicIds(new HashSet<>(topicMap.values()));
        }
        List<Point> existPoints = pointService.condition(pointCondition);
        if(CollUtil.isNotEmpty(existPoints)){
            existPoints.stream().collect(Collectors.groupingBy(Point::getTopicId)).forEach((topicId,points)->
                    topicPNamePIDMap.put(topicId,points.stream().collect(Collectors.toMap(Point::getTitle, Function.identity(),(t1, t2)->t1)))
            );
        }

        Map<String,Point> newPointMap = new HashMap<>();
        perRemovePointId = new HashSet<>();
        insertPoint = new ArrayList<>();
        insertSubPoint = new ArrayList<>();
        excelErrorDataList = new ArrayList<>();

        for (PointExcelDto excelDto : excelParseDataList) {
            if(StrUtil.isBlank(excelDto.getTopic())){
                excelDto.appendError("Topic Is Required!");
                excelErrorDataList.add(excelDto);
            }

            if(StrUtil.isBlank(excelDto.getTitle())){
                excelDto.appendError("Title Is Required!");
                excelErrorDataList.add(excelDto);
            }

            if(StrUtil.isBlank(excelDto.getSubPoint())){
                excelDto.appendError("SubPoint Is Required!");
                excelErrorDataList.add(excelDto);
            }

            if(StrUtil.isBlank(excelDto.getContent())){
                excelDto.appendError("Content Is Required!");
                excelErrorDataList.add(excelDto);
            }
        }
        if(CollUtil.isNotEmpty(excelErrorDataList)){
            return;
        }

        // group topic...
        Map<String, List<PointExcelDto>> topicParseMap = excelParseDataList.stream().collect(Collectors.groupingBy(PointExcelDto::getTopic));
        for (String topicCode : topicParseMap.keySet()) {
            List<PointExcelDto> points = topicParseMap.get(topicCode);
            Long topicId = topicMap.get(topicCode);
            if(topicId == null){
                points.forEach(p -> p.setError("Topic Code NotFound."));
                excelErrorDataList.addAll(points);
                continue;
            }
            Map<String, Point> pointTitleIdMap = topicPNamePIDMap.get(topicId);

            // group point...
            Map<String, List<PointExcelDto>> pointTitleMap = points.stream().collect(Collectors.groupingBy(PointExcelDto::getTitle));
            for (String pointTitle : pointTitleMap.keySet()) {
                List<PointExcelDto> subPints = pointTitleMap.get(pointTitle);
                boolean forceUpdate = subPints.stream().anyMatch(p -> "Y".equals(p.getForceUpdate()));
                Point point;
                if(CollUtil.isNotEmpty(pointTitleIdMap)){
                    point = pointTitleIdMap.get(pointTitle);
                    if(point != null){
                        if(!forceUpdate){
                            continue;
                        }
                        perRemovePointId.add(point.getId());
                    }
                }

                String pointKey = topicId + pointTitle;
                point = newPointMap.get(pointKey);
                if(point == null){
                    point = Point.builder().build();
                    point.setTitle(pointTitle);
                    point.setTopicId(topicId);
                    point.setDegreeOfMastery(DegreeOfMastery.UNDERSTAND);
                    newPointMap.put(pointKey,point);
                    insertPoint.add(point);
                }

                // sub point...
                for (PointExcelDto subPointExcel : subPints) {
                    point.setSubPointCount(point.getSubPointCount() == null?1:point.getSubPointCount()+1);
                    SubPoint subPoint = SubPoint.builder().build();
                    subPoint.setTitle(subPointExcel.getSubPoint());
                    subPoint.setDetailContent(subPointExcel.getContent());
                    subPoint.setPoint(point);
                    insertSubPoint.add(subPoint);
                }
            }


        }
    }
}

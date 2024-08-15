package com.zion.learning.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.response.PointVo;
import com.zion.common.vo.learning.response.TopicStatisticVo;
import com.zion.common.vo.learning.request.PointQO;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.common.PractiseResult;
import com.zion.learning.model.Point;
import com.zion.learning.model.Topic;
import com.zion.learning.service.excel.PointExcelDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public interface PointService {

    /**
     * 统计主题下学习程度
     * @param topicId
     * @return
     */
    TopicStatisticVo statisticMastery(Long topicId);

    /**
     * list with page
     * @param pointQO
     * @return
     */
    Page<PointVo> list(PointQO pointQO) ;

    /**
     * save or update
     * @param pointQO
     * @return
     */
    boolean save(PointQO pointQO) ;

    PointVo info(Long pointId);

    boolean delete(Long pointId);

    boolean upgrade(PractiseResult result, PointQO qo);

    /**
     * judge the topic id is have point
     * @param topicId topicId
     */
    boolean existPointByTopicId(Long topicId);

    /**
     * import Point with Excel
     * @param file excel
     */
    List<PointExcelDto> importByExcel(MultipartFile file,Long userId);

    /**
     * condition list
     */
    List<Point> condition(Point pointCondition);
}

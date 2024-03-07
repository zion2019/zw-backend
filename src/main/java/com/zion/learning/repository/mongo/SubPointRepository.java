package com.zion.learning.repository.mongo;

import cn.hutool.core.bean.BeanUtil;
import com.mongodb.client.result.UpdateResult;
import com.zion.common.basic.CommonConstant;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.resource.request.SubPointQO;
import com.zion.learning.dao.SubPointDao;
import com.zion.learning.model.Point;
import com.zion.learning.model.SubPoint;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Repository
public class SubPointRepository extends ZWMongoBasicRep<SubPoint> implements SubPointDao {

    @Override
    public void saveBatch(List<SubPointQO> subPointList) {
        List<SubPoint> subPoints = BeanUtil.copyToList(subPointList, SubPoint.class);

        // 批量删除
        SubPoint build = SubPoint.builder().build();
        build.setId(subPoints.get(0).getPointId());
        this.delete(build);

        subPoints.forEach(subPoint -> {
            BaseEntityUtil.filedBasicInfo(subPoint);
        });

        mongoTemplate.insert(subPoints,SubPoint.class);

    }

    @Override
    Query generateQuery(SubPoint condition) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(CommonConstant.DELETED_NO));

        if(condition.getPointId() != null){
            query.addCriteria(Criteria.where("pointId").is(condition.getPointId()));
        }
        return query;
    }
}

package com.zion.bill.repository;

import cn.hutool.core.text.CharSequenceUtil;
import com.zion.bill.dao.BillChannelDao;
import com.zion.bill.model.BillChannel;
import com.zion.learning.repository.mongo.ZWMongoBasicRep;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

@AllArgsConstructor
@Repository
public class BillChannelRepository extends ZWMongoBasicRep<BillChannel> implements BillChannelDao {

    @Override
    public Query generateQuery(BillChannel condition) {
        Query query = new Query();

        if (condition.getUserId() != null) {
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }

        if (CharSequenceUtil.isNotBlank(condition.getName())) {
            Pattern pattern = Pattern.compile(".*" + Pattern.quote(condition.getName()) + ".*", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(pattern));
        }

        if (condition.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(condition.getStatus()));
        }

        if (CharSequenceUtil.isNotBlank(condition.getSortFiledName())) {
            query.with(Sort.by(condition.getSortDirection(), condition.getSortFiledName()));
        }

        return query;
    }
}
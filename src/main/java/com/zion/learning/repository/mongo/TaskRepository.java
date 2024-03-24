package com.zion.learning.repository.mongo;

import com.zion.learning.dao.TaskDao;
import com.zion.learning.model.Task;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class TaskRepository  extends ZWMongoBasicRep<Task> implements TaskDao {

    @Override
    Query generateQuery(Task condition) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        if(condition.getFinished() != null){
            query.addCriteria(Criteria.where("finished").is(condition.getFinished()));
        }

        if(condition.getGtEndTime() != null){
            query.addCriteria(Criteria.where("endTime").gte(condition.getGtEndTime()));
        }
        return query;
    }
}

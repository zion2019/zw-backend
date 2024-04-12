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
        if(condition.getUserId() != null){
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if(condition.getFinished() != null){
            query.addCriteria(Criteria.where("finished").is(condition.getFinished()));
        }
        if(condition.getRemind() != null){
            query.addCriteria(Criteria.where("remind").is(condition.getRemind()));
        }
        if(condition.getFromTaskTime() != null && condition.getToTaskTime() != null){
            query.addCriteria(Criteria.where("taskTime").gte(condition.getFromTaskTime()).lte(condition.getToTaskTime()));
        }


        return query;
    }
}

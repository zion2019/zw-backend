package com.zion.learning.repository.mongo;

import com.zion.learning.dao.TaskOperationDao;
import com.zion.learning.model.TaskOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class TaskOperationRepository extends ZWMongoBasicRep<TaskOperation> implements TaskOperationDao {

    @Override
    public Query generateQuery(TaskOperation condition) {
        Query query = new Query();
        if(condition.getUserId() != null){
            query.addCriteria(Criteria.where("userId").is(condition.getUserId()));
        }
        if(condition.getType() != null){
            query.addCriteria(Criteria.where("type").is(condition.getTaskId()));
        }
        if(condition.getTaskId() != null){
            query.addCriteria(Criteria.where("taskId").is(condition.getTaskId()));
        }



        return query;
    }
}

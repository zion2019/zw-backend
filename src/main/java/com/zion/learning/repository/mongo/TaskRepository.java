package com.zion.learning.repository.mongo;

import com.zion.learning.dao.TaskDao;
import com.zion.learning.model.Task;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class TaskRepository  extends ZWMongoBasicRep<Task> implements TaskDao {

    @Override
    Query generateQuery(Task condition) {
        return null;
    }
}

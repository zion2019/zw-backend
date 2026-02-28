package com.zion.learning.dao.impl;

import com.zion.common.db.ZDaoMongoImpl;
import com.zion.learning.dao.SubjectDao;
import com.zion.learning.model.Subject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SubjectDaoMongoImpl extends ZDaoMongoImpl<Subject> implements SubjectDao {
    
    @Override
    public List<Long> getRecentlyTagIds(int num, Long userId) {
        // 匹配条件：未删除且属于指定用户
        MatchOperation matchOperation = Aggregation.match(
            Criteria.where("deleted").is(0)
                .and("userId").is(userId)
                .and("tagId").exists(true)
        );
        
        // 按tagId分组并统计数量
        GroupOperation groupOperation = Aggregation.group("tagId")
            .count().as("count")
            .first("tagId").as("tagId")
            .max("createdTime").as("lastUsedTime");
        
        // 按使用次数降序、最后使用时间降序排序
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "count", "lastUsedTime");
        
        // 限制返回数量
        Aggregation aggregation = Aggregation.newAggregation(
            matchOperation,
            groupOperation,
            sortOperation,
            Aggregation.limit(num)
        );
        
        AggregationResults<TagCountResult> results = mongoTemplate.aggregate(
            aggregation, 
            "subjects", 
            TagCountResult.class
        );
        
        return results.getMappedResults().stream()
            .map(TagCountResult::getTagId)
            .collect(Collectors.toList());
    }
    
    /**
     * 内部类用于聚合结果映射
     */
    private static class TagCountResult {
        private Long tagId;
        private Long count;
        private Object lastUsedTime;
        
        public Long getTagId() {
            return tagId;
        }
        
        public void setTagId(Long tagId) {
            this.tagId = tagId;
        }
        
        public Long getCount() {
            return count;
        }
        
        public void setCount(Long count) {
            this.count = count;
        }
        
        public Object getLastUsedTime() {
            return lastUsedTime;
        }
        
        public void setLastUsedTime(Object lastUsedTime) {
            this.lastUsedTime = lastUsedTime;
        }
    }
}
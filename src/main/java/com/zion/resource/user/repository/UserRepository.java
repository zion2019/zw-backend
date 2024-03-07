package com.zion.resource.user.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.resource.request.UserQO;
import com.zion.resource.user.dao.UserDao;
import com.zion.resource.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class UserRepository implements UserDao {

    private MongoTemplate mongoTemplate;

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public User save(User user) {
        BaseEntityUtil.filedBasicInfo(user);
        return mongoTemplate.save(user);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<User> condition(UserQO userQO) {
        Query query = new Query();

        // 根据UserQO对象中的条件设置查询条件
        if (userQO.getLoginName() != null) {
            query.addCriteria(Criteria.where("loginName").is(userQO.getLoginName()));
        }

        // 执行查询并返回符合条件的用户列表
        return mongoTemplate.find(query, User.class);
    }

    @Override
    public User conditionOne(UserQO userQO) {
        Query query = new Query();

        // 根据UserQO对象中的条件设置查询条件
        if (userQO.getLoginName() != null) {
            query.addCriteria(Criteria.where("loginName").is(userQO.getLoginName()));
        }

        // 执行查询并返回符合条件的用户列表
        List<User> users = mongoTemplate.find(query, User.class);
        if(!CollUtil.isEmpty(users)){
            return users.get(0);
        }

        return null;
    }

    @Override
    public void update(User user) {
        // Find the user by id
        User existingUser = mongoTemplate.findById(user.getId(), User.class);

        // If the user exists, update the non-null fields in the existing user
        if (existingUser != null) {
            if (CharSequenceUtil.isNotBlank(user.getLoginName())) {
                existingUser.setLoginName(user.getLoginName());
            }
            if (CharSequenceUtil.isNotBlank(user.getPassword())) {
                existingUser.setPassword(user.getPassword());
            }
            if (CharSequenceUtil.isNotBlank(user.getNickName())) {
                existingUser.setNickName(user.getNickName());
            }
            if (CharSequenceUtil.isNotBlank(user.getTelephone())) {
                existingUser.setTelephone(user.getTelephone());
            }
            if (CharSequenceUtil.isNotBlank(user.getEmail())) {
                existingUser.setEmail(user.getEmail());
            }
            // Update any other non-null fields in the same way
            BaseEntityUtil.filedBasicInfo(existingUser);
            // Save the updated user
            mongoTemplate.save(existingUser);
        }
    }


    // 其他自定义查询方法
}
package com.zion.resource.user.dao;

import com.zion.common.vo.resource.request.UserQO;
import com.zion.resource.user.model.User;

import java.util.List;


public interface UserDao {

    User findById(Long id);

    User save(User user);

    void delete(Long id);

    /**
     * query with condition
     * @param userQO
     * @return
     */
    List<User> condition(UserQO userQO);

    /**
     * query one record with condition
     * @param userQO
     * @return
     */
    User conditionOne(UserQO userQO);

    void update(User copyProperties);
}

package com.zion.resource.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.zion.common.basic.ServiceException;
import com.zion.common.db.ZCondition;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.resource.request.UserQO;
import com.zion.common.vo.resource.response.UserVO;
import com.zion.resource.user.dao.UserDao;
import com.zion.resource.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    public List<UserVO> condition(UserQO userQO) {
        List<UserVO> userVOS = new ArrayList<>();
        List<User> users = userDao.queryList(new ZCondition<User>()
                .eq(userQO.getId() != null, User::getId, userQO.getId())
                .eq(userQO.getLoginName() != null, User::getLoginName, userQO.getLoginName())
                .eq(userQO.getTelephone() != null, User::getTelephone, userQO.getTelephone())
                .eq(userQO.getEmail() != null, User::getEmail, userQO.getEmail()));
        if(CollUtil.isNotEmpty(users)){
            userVOS = BeanUtil.copyToList(users, UserVO.class);
        }
        return userVOS;
    }

    public UserVO conditionOne(UserQO userQO) {
        User user = userDao.queryOne(new ZCondition<User>()
                .eq(userQO.getId() != null, User::getId, userQO.getId())
                .eq(userQO.getLoginName() != null, User::getLoginName, userQO.getLoginName())
                .eq(userQO.getTelephone() != null, User::getTelephone, userQO.getTelephone())
                .eq(userQO.getEmail() != null, User::getEmail, userQO.getEmail()));
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    public boolean register(UserQO qo) {
        if(CharSequenceUtil.isBlank(qo.getLoginName())){
            throw new ServiceException("The loginName required.");
        }
        if(CharSequenceUtil.isBlank(qo.getTelephone()) && StrUtil.isBlank(qo.getEmail())){
            throw new ServiceException("Please be sure to leave a contact information for us!");
        }
        Assert.isTrue(CharSequenceUtil.isNotBlank(qo.getPassword()),"The password is required!");
        qo.setPassword("{noop}"+qo.getPassword());
        User existsUser = userDao.queryOne(new ZCondition<User>().eq(User::getLoginName, qo.getLoginName()));
        if(existsUser != null){
            throw new ServiceException("The loginName is exist.");
        }

        User user = BeanUtil.copyProperties(qo, User.class);
        userDao.save(user);
        return true;
    }

    public boolean update(UserQO qo) {
        Assert.isTrue(qo.getId() != null,"The userId required.");
        Assert.isTrue(CharSequenceUtil.isNotBlank(qo.getLoginName()),"The loginName required.");
        // Fixed the logic condition - was checking for both telephone blank AND email blank
        if (CharSequenceUtil.isBlank(qo.getTelephone()) && CharSequenceUtil.isBlank(qo.getEmail())) {
            throw new ServiceException("Please be sure to leave a contact information for us!");
        }

        User user = BeanUtil.copyProperties(qo, User.class);
        userDao.save(user);

        return true;
    }
}
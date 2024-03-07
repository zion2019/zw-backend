package com.zion.resource.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.zion.common.basic.ServiceException;
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
        List<User> users = userDao.condition(userQO);
        if(CollUtil.isNotEmpty(users)){
            userVOS = BeanUtil.copyToList(users,UserVO.class);
        }
        return userVOS;
    }

    public UserVO conditionOne(UserQO userQO) {
        User user = userDao.conditionOne(userQO);
        return BeanUtil.copyProperties(user,UserVO.class);
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
        User existsUser = userDao.conditionOne(UserQO.builder().loginName(qo.getLoginName()).build());
        if(existsUser != null){
            throw new ServiceException("The loginName is exist.");
        }

        userDao.save(BeanUtil.copyProperties(qo,User.class));
        return true;
    }

    public boolean update(UserQO qo) {
        Assert.isTrue(qo.getId() != null,"The userId required.");
        Assert.isTrue(CharSequenceUtil.isNotBlank(qo.getLoginName()),"The loginName required.");
        Assert.isTrue(CharSequenceUtil.isNotBlank(qo.getTelephone()) && CharSequenceUtil.isBlank(qo.getEmail()),"Please be sure to leave a contact information for us!");

        userDao.update(BeanUtil.copyProperties(qo,User.class));

        return true;
    }
}

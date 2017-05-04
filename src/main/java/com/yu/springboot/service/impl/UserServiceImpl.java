package com.yu.springboot.service.impl;

import com.yu.springboot.common.base.GenericDao;
import com.yu.springboot.common.base.GenericServiceImpl;
import com.yu.springboot.common.datasource.TargetDataSource;
import com.yu.springboot.dao.UserMapper;
import com.yu.springboot.model.User;
import com.yu.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-25
 */
@TargetDataSource(name = "boss")
@Service("UserService")
public class UserServiceImpl extends GenericServiceImpl implements UserService {

    @Autowired
    private UserMapper dao;

    @TargetDataSource(name = "smp")
    @Override
    public User findByName(String name) {
        return this.dao.findByName(name);
    }

    @Override
    public int total(Object paramObj){
        return this.dao.total(paramObj);
    }

    @Override
    public GenericDao getDao() {
        return this.dao;
    }
}

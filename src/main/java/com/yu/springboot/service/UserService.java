package com.yu.springboot.service;

import com.yu.springboot.common.base.GenericService;
import com.yu.springboot.model.User;

/**
 * @author Feng Yu
 * @version V1.0
 * @date 2017/4/25 0025
 */
public interface UserService extends GenericService{
    User findByName(String name);
}

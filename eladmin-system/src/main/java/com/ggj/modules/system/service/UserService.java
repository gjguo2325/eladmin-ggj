package com.ggj.modules.system.service;

import com.ggj.modules.system.service.dto.UserLoginDto;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

/**
 * @author guogj
 * @date 2022/06/24
 */
public interface UserService {

  /**
   * 根据用户名获取用户
   *
   * @param userName 用户名
   * @return UserLoginDto
   */
  UserLoginDto getLoginData(String userName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}

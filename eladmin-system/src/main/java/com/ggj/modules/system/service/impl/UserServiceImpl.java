package com.ggj.modules.system.service.impl;

import com.ggj.exception.EntityNotFoundException;
import com.ggj.modules.system.domain.User;
import com.ggj.modules.system.repository.UserRepository;
import com.ggj.modules.system.service.UserService;
import com.ggj.modules.system.service.dto.UserLoginDto;
import com.ggj.modules.system.service.mapstruct.UserLoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author guogj
 * @date 2022/06/24
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserLoginDto getLoginData(String userName) {
    User user = userRepository.findByUsername(userName);
    if(Objects.isNull(user)){
      throw new EntityNotFoundException(User.class, "name", userName);
    }
    return UserLoginDto.from(user);
  }
}

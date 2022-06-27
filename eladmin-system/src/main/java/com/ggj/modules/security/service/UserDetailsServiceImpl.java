package com.ggj.modules.security.service;

import com.ggj.exception.BadRequestException;
import com.ggj.modules.security.service.dto.JwtUserDto;
import com.ggj.modules.system.service.DataService;
import com.ggj.modules.system.service.RoleService;
import com.ggj.modules.system.service.UserService;
import com.ggj.modules.system.service.dto.UserLoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author guogj
 * @date 2022/06/24
 */
@Slf4j
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserCacheManager userCacheManager;
  private final UserService userService;
  private final DataService dataService;
  private final RoleService roleService;

  @Override
  public UserDetails loadUserByUsername(String username){
    JwtUserDto jwtUserDto = userCacheManager.getUserCache(username);
    if(Objects.isNull(jwtUserDto)){
      UserLoginDto user;
      try {
        user = userService.getLoginData(username);
      }catch (Exception e) {
        // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
        throw new UsernameNotFoundException(username, e);
      }
      if(Objects.isNull(user)){
        throw new UsernameNotFoundException("没有当前用户");
      }else {
        if(!user.getEnabled()){
          throw new BadRequestException("账号未激活");
        }
        jwtUserDto = new JwtUserDto(
            user, dataService.getDeptIds(user), roleService.mapToGrantedAuthorities(user)
        );
        //添加缓存
        userCacheManager.addUserCache(username, jwtUserDto);
      }
    }
    return jwtUserDto;
  }
}

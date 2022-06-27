package com.ggj.modules.security.service;

import com.ggj.modules.security.config.bean.SecurityProperties;
import com.ggj.modules.security.service.dto.JwtUserDto;
import com.ggj.modules.security.service.dto.OnlineUserDto;
import com.ggj.utils.EncryptUtils;
import com.ggj.utils.RedisUtils;
import com.ggj.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author guogj
 * @date 2022/06/24
 */
@Service
@Slf4j
public class OnlineUserService {

  private final RedisUtils redisUtils;
  private final SecurityProperties securityProperties;

  public OnlineUserService(RedisUtils redisUtils, SecurityProperties securityProperties) {
    this.redisUtils = redisUtils;
    this.securityProperties = securityProperties;
  }

  public void save(JwtUserDto jwtDto, String token, HttpServletRequest request){
    String dept = jwtDto.getUser().getDept().getName();
    String ip = StringUtils.getIp(request);
    String browser = StringUtils.getBrowser(request);
    String address = StringUtils.getCityInfo(ip);
    OnlineUserDto onlineUserDto = null;
    try {
      onlineUserDto = new OnlineUserDto(jwtDto.getUsername(), jwtDto.getUser().getNickName(), dept, browser , ip, address, EncryptUtils.desEncrypt(token), new Date() );
    }catch (Exception e) {
      log.error(e.getMessage(),e);
    }
    redisUtils.set(securityProperties.getOnlineKey() + token, onlineUserDto, securityProperties.getTokenValidityInSeconds()/1000);
  }

  /**
   * 检测用户是否在之前已经登录，已经登录踢下线
   * @param userName 用户名
   */
  public void checkLoginOnUser(String userName, String igoreToken){
    List<OnlineUserDto> onlineUserDtos = getAll(userName);
    if(onlineUserDtos ==null || onlineUserDtos.isEmpty()){
      return;
    }
    for(OnlineUserDto onlineUserDto : onlineUserDtos){
      if(onlineUserDto.getUserName().equals(userName)){
        try {
          String token =EncryptUtils.desDecrypt(onlineUserDto.getKey());
          if(StringUtils.isNotBlank(igoreToken)&&!igoreToken.equals(token)){
            this.kickOut(token);
          }else if(StringUtils.isBlank(igoreToken)){
            this.kickOut(token);
          }
        } catch (Exception e) {
          log.error("checkUser is error",e);
        }
      }
    }
  }

  /**
   * 踢出用户
   * @param key /
   */
  public void kickOut(String key){
    key = securityProperties.getOnlineKey() + key;
    redisUtils.del(key);
  }

  /**
   * 查询全部数据，不分页
   * @param filter /
   * @return /
   */
  public List<OnlineUserDto> getAll(String filter){
    List<String> keys = redisUtils.scan(securityProperties.getOnlineKey() + "*");
    Collections.reverse(keys);
    List<OnlineUserDto> onlineUserDtos = new ArrayList<>();
    for (String key : keys) {
      OnlineUserDto onlineUserDto = (OnlineUserDto) redisUtils.get(key);
      if(StringUtils.isNotBlank(filter)){
        if(onlineUserDto.toString().contains(filter)){
          onlineUserDtos.add(onlineUserDto);
        }
      } else {
        onlineUserDtos.add(onlineUserDto);
      }
    }
    onlineUserDtos.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
    return onlineUserDtos;
  }

  /**
   * 查询用户
   * @param key /
   * @return /
   */
  public OnlineUserDto getOne(String key) {
    return (OnlineUserDto)redisUtils.get(key);
  }

  /**
   * 退出登录
   * @param token /
   */
  public void logout(String token) {
    String key = securityProperties.getOnlineKey() + token;
    redisUtils.del(key);
  }
}

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
import java.util.Date;

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

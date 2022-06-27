package com.ggj.modules.security.rest;

import cn.hutool.core.util.IdUtil;
import com.ggj.annotation.rest.AnonymousDeleteMapping;
import com.ggj.annotation.rest.AnonymousGetMapping;
import com.ggj.annotation.rest.AnonymousPostMapping;
import com.ggj.config.RsaProperties;
import com.ggj.exception.BadRequestException;
import com.ggj.modules.security.config.bean.LoginCodeEnum;
import com.ggj.modules.security.config.bean.LoginProperties;
import com.ggj.modules.security.config.bean.SecurityProperties;
import com.ggj.modules.security.security.TokenProvider;
import com.ggj.modules.security.service.OnlineUserService;
import com.ggj.modules.security.service.dto.AuthUserDto;
import com.ggj.modules.security.service.dto.JwtUserDto;
import com.ggj.utils.RedisUtils;
import com.ggj.utils.RsaUtils;
import com.ggj.utils.SecurityUtils;
import com.ggj.utils.StringUtils;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author guogj
 * @date 2022/06/23
 */
@Api(tags = "系统：系统授权接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthorizationController {

  private final LoginProperties loginProperties;
  private final SecurityProperties securityProperties;
  private final RedisUtils redisUtils;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;
  private final OnlineUserService onlineUserService;

  @ApiOperation("获取验证码")
  @AnonymousGetMapping(value = "/code")
  public ResponseEntity<Object> getCode(){
    // 获取运算结果
    Captcha captcha = loginProperties.getCaptcha();
    String uuid = securityProperties.getCodeKey() + IdUtil.randomUUID();
    String captchaValue = captcha.text();
    if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
      captchaValue = captchaValue.split("\\.")[0];
    }
    // 保存到redis缓存中
    redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(),TimeUnit.MINUTES);
    //验证码信息
    Map<String,Object> imgResult = new HashMap<String, Object>(2){{
      put("img", captcha.toBase64());
      put("uuid", uuid);
    }};
    return ResponseEntity.ok(imgResult);
  }

  @ApiOperation(value = "获取用户信息")
  @GetMapping(value = "/info")
  public ResponseEntity<Object> getInfo(){
    return ResponseEntity.ok(SecurityUtils.getCurrentUser());
  }

  @ApiOperation(value = "登录授权")
  @AnonymousPostMapping(value = "/login")
  public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
    //密码解密
    String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
    //查询验证码
    String code = (String) redisUtils.get(authUser.getUuid());
    //清除验证码
    redisUtils.del(authUser.getUuid());
    if(StringUtils.isBlank(code)){
      throw new BadRequestException("验证码不存在或已过期");
    }
    if(StringUtils.isBlank(code)|| !authUser.getCode().equalsIgnoreCase(code)){
      throw new BadRequestException("验证码错误");
    }
    //验证信息
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authUser.getUsername(),password);
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    //保存了用户认证成功后的认证信息（用户名，密码，权限等）
    SecurityContextHolder.getContext().setAuthentication(authentication);
    //创建token
    String token = tokenProvider.createToken(authentication);
    final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
    // FIXME: 2022/6/24 保存在线信息
    onlineUserService.save(jwtUserDto, token, request);
    // 返回token与用户信息
    Map<String, Object> authInfo = new HashMap<String, Object>(2){{
      put("token", token);
      put("user", jwtUserDto);
    }};
    // FIXME: 2022/6/24 后期添加单点登录
    if (loginProperties.isSingleLogin()) {
      //踢掉之前已经登录的token
      onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
    }
    return ResponseEntity.ok(authInfo);
  }

  @ApiOperation("退出登录")
  @AnonymousDeleteMapping(value = "/logout")
  public ResponseEntity<Object> logout(HttpServletRequest request) {
    onlineUserService.logout(tokenProvider.getToken(request));
    return new ResponseEntity<>(HttpStatus.OK);
  }
}

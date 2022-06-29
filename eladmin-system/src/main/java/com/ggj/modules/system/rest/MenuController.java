package com.ggj.modules.system.rest;

import com.ggj.modules.system.service.MenuService;
import com.ggj.modules.system.service.dto.MenuDto;
import com.ggj.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author guogj
 * @date 2022/06/29
 */
@RequiredArgsConstructor
@RestController
@Api(tags = "系统：菜单权限")
@RequestMapping("/api/menus")
public class MenuController {

  private final MenuService menuService;

  @GetMapping(value = "/build")
  @ApiOperation("获取前端所需菜单")
  public ResponseEntity<Object> buildMenus(){
    List<MenuDto> menuDtoList = menuService.findByUser(SecurityUtils.getCurrentUserId());
    List<MenuDto>menuDtos = menuService.buildTree(menuDtoList);
    return new ResponseEntity<>(menuService.buildMenus(menuDtos), HttpStatus.OK);
  }
}

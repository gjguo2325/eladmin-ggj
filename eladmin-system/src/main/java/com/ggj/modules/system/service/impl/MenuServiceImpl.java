/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.ggj.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ggj.modules.system.domain.Menu;
import com.ggj.modules.system.domain.vo.MenuMetaVo;
import com.ggj.modules.system.domain.vo.MenuVo;
import com.ggj.modules.system.repository.MenuRepository;
import com.ggj.modules.system.repository.UserRepository;
import com.ggj.modules.system.service.MenuService;
import com.ggj.modules.system.service.RoleService;
import com.ggj.modules.system.service.dto.MenuDto;
import com.ggj.modules.system.service.dto.RoleSmallDto;
import com.ggj.modules.system.service.mapstruct.MenuMapper;
import com.ggj.utils.RedisUtils;
import com.ggj.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "menu")
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final MenuMapper menuMapper;
    private final RoleService roleService;
    private final RedisUtils redisUtils;

    /**
     * 用户角色改变时需清理缓存
     * @param currentUserId /
     * @return /
     */
    @Override
    @Cacheable(key = "'user:' + #p0")
    public List<MenuDto> findByUser(Long currentUserId) {
        List<RoleSmallDto> roles = roleService.findByUsersId(currentUserId);
        Set<Long> roleIds = roles.stream().map(RoleSmallDto::getId).collect(Collectors.toSet());
        LinkedHashSet<Menu> menus = menuRepository.findByRoleIdsAndTypeNot(roleIds, 2);
        return menus.stream().map(menuMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<MenuDto> buildTree(List<MenuDto> menuDtos) {
        List<MenuDto> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (MenuDto menuDTO : menuDtos) {
            if (menuDTO.getPid() == null) {
                trees.add(menuDTO);
            }
            for (MenuDto it : menuDtos) {
                if (menuDTO.getId().equals(it.getPid())) {
                    if (menuDTO.getChildren() == null) {
                        menuDTO.setChildren(new ArrayList<>());
                    }
                    menuDTO.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        if(trees.size() == 0){
            trees = menuDtos.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        }
        return trees;
    }

    @Override
    public List<MenuVo> buildMenus(List<MenuDto> menuDtos) {
        List<MenuVo> list = new LinkedList<>();
        menuDtos.forEach(menuDTO -> {
                if (menuDTO!=null){
                    List<MenuDto> menuDtoList = menuDTO.getChildren();
                    MenuVo menuVo = new MenuVo();
                    menuVo.setName(ObjectUtil.isNotEmpty(menuDTO.getComponentName())  ? menuDTO.getComponentName() : menuDTO.getTitle());
                    // 一级目录需要加斜杠，不然会报警告
                    menuVo.setPath(menuDTO.getPid() == null ? "/" + menuDTO.getPath() :menuDTO.getPath());
                    menuVo.setHidden(menuDTO.getHidden());
                    // 如果不是外链
                    if(!menuDTO.getIFrame()){
                        if(menuDTO.getPid() == null){
                            menuVo.setComponent(StringUtils.isEmpty(menuDTO.getComponent())?"Layout":menuDTO.getComponent());
                            // 如果不是一级菜单，并且菜单类型为目录，则代表是多级菜单
                        }else if(menuDTO.getType() == 0){
                            menuVo.setComponent(StringUtils.isEmpty(menuDTO.getComponent())?"ParentView":menuDTO.getComponent());
                        }else if(StringUtils.isNoneBlank(menuDTO.getComponent())){
                            menuVo.setComponent(menuDTO.getComponent());
                        }
                    }
                    menuVo.setMeta(new MenuMetaVo(menuDTO.getTitle(),menuDTO.getIcon(),!menuDTO.getCache()));
                    if(CollectionUtil.isNotEmpty(menuDtoList)){
                        menuVo.setAlwaysShow(true);
                        menuVo.setRedirect("noredirect");
                        menuVo.setChildren(buildMenus(menuDtoList));
                        // 处理是一级菜单并且没有子菜单的情况
                    } else if(menuDTO.getPid() == null){
                        MenuVo menuVo1 = new MenuVo();
                        menuVo1.setMeta(menuVo.getMeta());
                        // 非外链
                        if(!menuDTO.getIFrame()){
                            menuVo1.setPath("index");
                            menuVo1.setName(menuVo.getName());
                            menuVo1.setComponent(menuVo.getComponent());
                        } else {
                            menuVo1.setPath(menuDTO.getPath());
                        }
                        menuVo.setName(null);
                        menuVo.setMeta(null);
                        menuVo.setComponent("Layout");
                        List<MenuVo> list1 = new ArrayList<>();
                        list1.add(menuVo1);
                        menuVo.setChildren(list1);
                    }
                    list.add(menuVo);
                }
            }
        );
        return list;
    }
}

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
package com.ggj.modules.system.service;

import com.ggj.modules.security.service.dto.AuthorityDto;
import com.ggj.modules.system.service.dto.RoleSmallDto;
import com.ggj.modules.system.service.dto.UserDto;

import java.util.List;

/**
 * @author Zheng Jie
 * @date 2018-12-03
 */
public interface RoleService {

    /**
     * 根据用户ID查询
     * @param id 用户ID
     * @return /
     */
    List<RoleSmallDto> findByUsersId(Long id);

    /**
     * 获取用户权限信息
     * @param user 用户信息
     * @return 权限信息
     */
    List<AuthorityDto> mapToGrantedAuthorities(UserDto user);
}

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

import com.ggj.modules.system.domain.Dept;

import java.util.List;
import java.util.Set;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
public interface DeptService {

    /**
     * 根据角色ID查询
     * @param id /
     * @return /
     */
    Set<Dept> findByRoleId(Long id);

    /**
     * 根据PID查询
     * @param pid /
     * @return /
     */
    List<Dept> findByPid(long pid);

    /**
     * 获取
     * @param deptList
     * @return
     */
    List<Long> getDeptChildren(List<Dept> deptList);

    /**
     * 部门信息
     * @param dept dept
     */
    void create(Dept dept);
}

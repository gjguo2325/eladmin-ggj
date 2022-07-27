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

import com.ggj.modules.system.domain.Dept;
import com.ggj.modules.system.domain.User;
import com.ggj.modules.system.repository.DeptRepository;
import com.ggj.modules.system.repository.UserRepository;
import com.ggj.modules.system.service.DeptService;
import com.ggj.utils.CacheKey;
import com.ggj.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Zheng Jie
* @date 2019-03-25
*/
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dept")
public class DeptServiceImpl implements DeptService {

    private final DeptRepository deptRepository;
    private final RedisUtils redisUtils;
    private final UserRepository userRepository;

    @Override
    public List<Dept> findByPid(long pid) {
        return deptRepository.findByPid(pid);
    }

    @Override
    public Set<Dept> findByRoleId(Long id) {
        return deptRepository.findByRoleId(id);
    }

    @Override
    public List<Long> getDeptChildren(List<Dept> deptList) {
        List<Long> list = new ArrayList<>();
        deptList.forEach(dept -> {
                    if (dept!=null && dept.getEnabled()) {
                        List<Dept> depts = deptRepository.findByPid(dept.getId());
                        if (depts.size() != 0) {
                            list.addAll(getDeptChildren(depts));
                        }
                        list.add(dept.getId());
                    }
                }
        );
        return list;
    }

    @Override
    public void create(Dept dept) {
        deptRepository.save(dept);
        //计算子节点数量
        dept.setSubCount(0);
        //清理缓存
        updateSubCount(dept.getPid());
        // 清理自定义角色权限的datascope缓存
        delCaches(dept.getPid());
    }

    public void updateSubCount(Long deptId){
        if (deptId != null) {
            int count = deptRepository.countByPid(deptId);
            deptRepository.updateSubCntById(deptId, count);
        }
    }

    /**
     * 清理缓存
     * @param id /
     */
    public void delCaches(Long id){
        List<User> users = userRepository.findByRoleDeptId(id);
        // 删除数据权限
        redisUtils.delByKeys(CacheKey.DATA_USER, users.stream().map(User::getId).collect(Collectors.toSet()));
        redisUtils.del(CacheKey.DEPT_ID + id);
    }
}

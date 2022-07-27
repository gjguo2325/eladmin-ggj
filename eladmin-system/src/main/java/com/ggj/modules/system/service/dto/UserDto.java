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
package com.ggj.modules.system.service.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.ggj.base.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 用户dto
 *
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Getter
@Setter
public class UserDto extends BaseDTO implements Serializable {

    private Long id;
    /**
     * 角色
     */
    private Set<RoleSmallDto> roles;
    /**
     * 岗位
     */
    private Set<JobSmallDto> jobs;
    /**
     * 部门
     */
    private DeptSmallDto dept;
    /**
     * 部门id
     */
    private Long deptId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 性别
     */
    private String gender;
    /**
     * 头像名称
     */
    private String avatarName;
    /**
     * 头像路径
     */
    private String avatarPath;
    /**
     * 密码
     */
    @JSONField(serialize = false)
    private String password;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 是否是admin超级管理员
     */
    @JSONField(serialize = false)
    private Boolean isAdmin = false;
    /**
     * 密码重置时间
     */
    private Date pwdResetTime;
}

package com.ggj.modules.system.repository;

import com.ggj.modules.system.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author guogj
 * @date 2022/06/24
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

   /**
    * 根据用户名查询
    *
    * @param userName 用户名
    * @return 用户
    */
   User findByUsername(String userName);

   /**
    * 根据角色中的部门查询
    * @param deptId /
    * @return /
    */
   @Query(value = "SELECT u.* FROM sys_user u, sys_users_roles r, sys_roles_depts d WHERE " +
       "u.user_id = r.user_id AND r.role_id = d.role_id AND d.dept_id = ?1 group by u.user_id", nativeQuery = true)
   List<User> findByRoleDeptId(Long deptId);
}

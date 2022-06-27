package com.ggj.modules.system.repository;

import com.ggj.modules.system.domain.User;
import com.ggj.modules.system.service.dto.UserLoginDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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
}

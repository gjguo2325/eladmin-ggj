package com.ggj.modules.system.rest;

import com.ggj.exception.BadRequestException;
import com.ggj.modules.system.domain.Dept;
import com.ggj.modules.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门 Rest Api
 *
 * @author guogj
 * @date 2022/06/30
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "系统：部门管理")
@RequestMapping("/api/dept")
public class DeptController {

  private final DeptService deptService;

  @ApiOperation("新增部门")
  @PostMapping
  @PreAuthorize("@el.check('dept:add')")
  public ResponseEntity<Object> createDept(@Validated @RequestBody Dept dept){
    if (null != dept.getId()) {
      throw new BadRequestException("已存在该部门");
    }
    deptService.create(dept);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}

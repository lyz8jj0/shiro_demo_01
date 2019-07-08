package com.ray.shiro_demo_01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ray.shiro_demo_01.entity.SysPermission;
import com.ray.shiro_demo_01.entity.SysRole;
import com.ray.shiro_demo_01.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Created by 李新宇
 * 2019-07-06 11:43
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

}

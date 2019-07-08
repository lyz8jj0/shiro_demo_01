package com.ray.shiro_demo_01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ray.shiro_demo_01.entity.SysPermission;
import com.ray.shiro_demo_01.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Created by 李新宇
 * 2019-07-06 11:43
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /**
     * 根据sysRoles查询权限 - 一个角色可能存在多个权限，故用Set
     */
    Set<SysPermission> myFindSysPermissionsBySysRoleId(@Param("sysRoles") Set<SysRole> sysRoles);
}

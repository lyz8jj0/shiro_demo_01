package com.ray.shiro_demo_01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ray.shiro_demo_01.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Created by 李新宇
 * 2019-07-06 11:59
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 根据uid查询角色信息 - 一个用户可能存在多个角色，故用Set
     */
    Set<SysRole> myFindSysRolesByUserInfoId(@Param("uid") Integer id);
}

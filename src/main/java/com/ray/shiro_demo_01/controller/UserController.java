package com.ray.shiro_demo_01.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ray.shiro_demo_01.config.ShiroRealm;
import com.ray.shiro_demo_01.entity.SysRolePermission;
import com.ray.shiro_demo_01.entity.UserInfo;
import com.ray.shiro_demo_01.mapper.UserInfoMapper;
import com.ray.shiro_demo_01.service.SysRolePermissionService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 李新宇
 * 2019-07-06 11:29
 */
@RestController
@RequestMapping("userInfo")
public class UserController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    /**
     * 创建固定写死的用户
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String login(Model model) {
        UserInfo user = new UserInfo();
        user.setName("波波");
        user.setIdCardNum("177777777777777777");
        user.setUsername("bobo");

        userInfoMapper.myInsert(user);

        return "创建用户成功";
    }

    /**
     * 删除固定写死的用户
     */
    @RequiresPermissions("userInfo:del")
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public String del(Model model) {
        userInfoMapper.myDel("bobo");

        return "删除用户名 bobo 成功";
    }

    /**
     * 用户列表
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String view(Model model) {
        return "用户列表";
    }


    /**
     * 添加 ray 用户 sysUser:del 权限
     */
    @GetMapping("/addPermission")
    public String addPermission(Model model) {

        // 在sys_role_permission 表中  将 删除的权限 关联到 ray 用户所在的角色
        SysRolePermission sysRolePermission = new SysRolePermission().setRoleId(3).setPermissionId(4);
        sysRolePermissionService.save(sysRolePermission);

        // 添加成功之后 清除缓存
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        ShiroRealm shiroRealm = (ShiroRealm) securityManager.getRealms().iterator().next();

        // 清除权限 相关的缓存
        shiroRealm.clearAllCache();

        return "添加 ray 用户 sysUser:del 权限成功";
    }

    /**
     * 删除 ray 用户 sysUser:del 权限
     */
    @GetMapping("/delPermission")
    public String delPermission(Model model) {

        // 在sys_role_permission 表中  将 删除的权限 关联到 ray 用户所在的角色
        sysRolePermissionService.remove(new QueryWrapper<SysRolePermission>().eq("role_id", 3).eq("permission_id", 4));

        // 删除成功之后 清除缓存
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        ShiroRealm shiroRealm = (ShiroRealm) securityManager.getRealms().iterator().next();

        // 清除权限 相关的缓存
        shiroRealm.clearAllCache();

        return "删除 ray 用户 sysUser:del 权限成功";
    }

}

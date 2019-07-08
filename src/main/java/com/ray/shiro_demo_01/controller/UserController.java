package com.ray.shiro_demo_01.controller;

import com.ray.shiro_demo_01.entity.UserInfo;
import com.ray.shiro_demo_01.mapper.UserInfoMapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
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


}

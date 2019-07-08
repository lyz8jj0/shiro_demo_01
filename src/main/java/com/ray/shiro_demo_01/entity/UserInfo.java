package com.ray.shiro_demo_01.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 李新宇
 * 2019-07-06 11:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 用户真实姓名
     */
    private String name;

    /**
     * 用户身份证号
     */
    private String idCardNum;

    /**
     * 用户状态 0：正常 1：锁定
     */
    private String state;

    @TableField(exist = false)
    private Set<SysRole> roles = new HashSet<>();
}

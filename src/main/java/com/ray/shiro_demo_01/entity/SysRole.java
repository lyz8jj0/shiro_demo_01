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
 * 2019-07-06 11:03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 是否可用 0:可用  1:不可用
     */
    private String available;

    /**
     * 角色标识程序中判断使用，如admin
     */
    private String role;

    /**
     * 角色描述，UI界面显示使用
     */
    private String description;

    @TableField(exist = false)
    private Set<UserInfo> users = new HashSet<>();
    @TableField(exist = false)
    private Set<SysPermission> permissions = new HashSet<>();
}

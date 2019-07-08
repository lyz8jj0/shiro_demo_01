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
 * 2019-07-06 11:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父编号，本权限可能是该父编号权限的子权限
     */
    private Integer parentId;

    /**
     * 父编号列表
     */
    private String parentIds;

    /**
     * 权限字符串，menu例子：role:* -- button例子：role:create,role:update,role:delete,role:view
     */
    private String permission;

    /**
     * 资源类型 [menu | button]
     */
    private String resourceType;

    /**
     * 资源路径 如：/userinfo/list
     */
    private String url;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 是否可用 0：可用  1：不可用
     */
    private String available;

    @TableField(exist = false)
    private Set<SysRole> sysRoles = new HashSet<>();
}

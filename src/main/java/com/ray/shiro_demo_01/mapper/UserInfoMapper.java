package com.ray.shiro_demo_01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ray.shiro_demo_01.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 李新宇
 * 2019-07-06 11:47
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {
    /**
     * 根据username查询信息
     */
    UserInfo myFindByUserInfoName(String username);

    /**
     * 根据userInfo插入信息
     */
    int myInsert(UserInfo userInfo);

    /**
     * 根据username删除信息
     */
    int myDel(@Param("username") String username);
}

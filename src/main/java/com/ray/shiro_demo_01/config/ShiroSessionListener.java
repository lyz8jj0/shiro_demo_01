package com.ray.shiro_demo_01.config;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配置session监听器
 * Created by 李新宇
 * 2019-07-22 10:20
 */
public class ShiroSessionListener implements SessionListener {

    /**
     * 统计在线人数
     * juc包下 线程安全自增
     */
    private AtomicInteger sessionCount = new AtomicInteger(0);

    /**
     * 会话创建时触发
     */
    @Override
    public void onStart(Session session) {
        // 会话创建，在线人数+1
        sessionCount.incrementAndGet();
    }

    /**
     * 会话退出时触发
     */
    @Override
    public void onStop(Session session) {
        // 会话退出，在线人数-1
        sessionCount.decrementAndGet();
    }

    /**
     * 会话过期时触发
     */
    @Override
    public void onExpiration(Session session) {
        // 会话过期，在线人数-1
        sessionCount.decrementAndGet();
    }

    /**
     * 获取在线人数使用
     */
    public AtomicInteger getSessionCount() {
        return sessionCount;
    }
}

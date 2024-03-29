package com.ray.shiro_demo_01.config;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by 李新宇
 * 2019-07-06 11:50
 * Shiro 配置类
 */
@Configuration
public class ShiroConfig {
    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     *
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }


    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * Web应用中,Shiro可控制的Web请求必须经过Shiro主过滤器的拦截
     *
     * @param securityManager
     * @return
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager， Shiro的核心安全接口
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 这里的 /login 是后台的接口名，非页面，如果不设置默认会自动寻找Web工程根目录下的 "/login.html" 页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 这里的 /index 是后台的接口名，非页面，登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        // 未授权跳转的链接
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

//        //自定义拦截器限制并发人数,参考博客：
//        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
//        //限制同一帐号同时在线的个数
//        filtersMap.put("kickout", kickoutSessionControlFilter());
//        shiroFilterFactoryBean.setFilters(filtersMap);


        //自定义拦截器限制并发人数,参考博客：
        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
        //限制同一帐号同时在线的个数
        filtersMap.put("kickout", kickoutSessionControlFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);

        // 配置访问权限 必须是LinkedHashMap，因为它必须保证有序
        // 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 一定要注意顺序,否则就不好使了
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //配置不登录可以访问的资源，anon 表示资源都可以匿名访问
        filterChainDefinitionMap.put("/login", "kickout,anon");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
//        filterChainDefinitionMap.put("/druid/**", "anon");
        // logout 是 shiro提供的过滤器
        filterChainDefinitionMap.put("/logout", "logout");

        //此时访问/userInfo/del需要del权限,在自定义Realm中为用户授权。
        filterChainDefinitionMap.put("/userInfo/del", "perms[userInfo:del]");
        filterChainDefinitionMap.put("/userInfo/add", "perms[userInfo:add]");
        filterChainDefinitionMap.put("/userInfo/view", "perms[userInfo:view]");

        //其他资源都需要认证  authc 表示需要认证才能进行访问
//        filterChainDefinitionMap.put("/**", "authc");

        //其他资源都需要认证  authc 表示需要认证才能进行访问  user表示配置记住我或认证通过可以访问的地址
        filterChainDefinitionMap.put("/**", "kickout,user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 配置核心安全事务管理器
     * 注意：securityManager()改变，setRealm(shiroRealm())改变
     *
     * @return
     */
    @Bean(name = "securityManager")
    public SecurityManager securityManager() {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 设置自定义realm
        securityManager.setRealm(shiroRealm());

        //配置记住我 参考博客：
        securityManager.setRememberMeManager(rememberMeManager());

        //配置 redis缓存管理器 参考博客：
        securityManager.setCacheManager(getEhCacheManager());

        //配置自定义session管理，使用redis 参考博客：
        securityManager.setSessionManager(sessionManager());

        return securityManager;
    }

    /**
     * 配置Shiro生命周期处理器
     *
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 身份认证realm; (这个需要自己写，账号密码校验；权限等)
     *
     * @return
     */
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
//        shiroRealm.setCachingEnabled(true);

        // 启动身份验证缓存，即缓存AuthenticationInfo信息，默认false
        shiroRealm.setAuthenticationCachingEnabled(true);
        //缓存AuthenticationInfo信息的缓存名称 在ehcache-shiro.xml中有对应缓存的配置
        shiroRealm.setAuthenticationCacheName("authenticationCache");

        //启用授权缓存，即缓存AuthorizationInfo信息，默认false
        shiroRealm.setAuthorizationCachingEnabled(true);
        //缓存AuthorizationInfo信息的缓存名称  在ehcache-shiro.xml中有对应缓存的配置
        shiroRealm.setAuthorizationCacheName("authorizationCache");
        return shiroRealm;
    }

    /**
     * 必须（thymeleaf页面使用shiro标签控制按钮是否显示）
     * 未引入thymeleaf包，Caused by: java.lang.ClassNotFoundException: org.thymeleaf.dialect.AbstractProcessorDialect
     *
     * @return
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    //授权缓存管理器
    @Bean
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
        return em;
    }

    /**
     * 解决 spring-boot Whitelabel Error Page
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryWebServerFactoryCustomizer() {
        return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
            @Override
            public void customize(ConfigurableWebServerFactory factory) {
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
                ErrorPage error500Page = new ErrorPage(HttpStatus.NOT_FOUND, "/500.html");
                factory.addErrorPages(error404Page, error500Page);
            }
        };
    }

    /**
     * 记住我cookie
     * cookie对象;会话Cookie模板 ,默认为: JSESSIONID 问题: 与SERVLET容器名冲突,重新定义为sid或rememberMe，自定义
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");

        //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");

        // 记住我cookie生效时间30天,单位秒
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    /**
     * 记住我管理器
     * cookie管理对象;记住我功能,rememberMe管理器
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    /**
     * 记住我Filter
     * FormAuthenticationFilter 过滤器 过滤记住我
     */
    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {
        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        //对应前端的checkbox的name = rememberMe
        formAuthenticationFilter.setRememberMeParam("rememberMe");
        return formAuthenticationFilter;
    }

    /**
     * 让某个实例的某个方法的返回值注入为Bean的实例
     * Spring静态注入
     *
     * @return
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(new Object[]{securityManager()});
        return factoryBean;
    }

    /**
     * 配置session监听
     */
    @Bean
    public ShiroSessionListener sessionListener() {
        ShiroSessionListener sessionListener = new ShiroSessionListener();
        return sessionListener;
    }

    /**
     * 配置会话ID生成器
     */
    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    /**
     * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
     * MemorySessionDAO 直接在内存中进行会话维护
     * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
     */
    @Bean
    public SessionDAO sessionDAO() {
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        // 使用 ehCacheManager
        enterpriseCacheSessionDAO.setCacheManager(getEhCacheManager());
        // 设置 session 缓存的名字，默认为 shiro-activeSessionCache
        enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
        // sessionId 生成器
        enterpriseCacheSessionDAO.setSessionIdGenerator(sessionIdGenerator());

        return enterpriseCacheSessionDAO;
    }

    /**
     * 配置保存sessionId的cookie
     * 注意：这里的cookie 不是上面的记住我 cookie 记住我需要一个cookie session管理 也需要自己的cookie
     */
    @Bean
    public SimpleCookie sessionIdCookie() {
        // 这个参数是cookie的名称
        SimpleCookie simpleCookie = new SimpleCookie("sid");

        //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：

        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");

        // maxAge=-1 表示浏览器关闭时失效此Cookie
        simpleCookie.setMaxAge(-1);

        return simpleCookie;
    }

    /**
     * 配置会话管理器，设定会话超时及保存
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        Collection<SessionListener> listeners = new ArrayList<>();

        // 配置监听
        listeners.add(sessionListener());

        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setCacheManager(getEhCacheManager());

        // 全局会话超时时间（单位毫秒 1:1000），默认30分钟 暂时设置为10秒，用于测试
//        sessionManager.setGlobalSessionTimeout(10000);
        // 是否开启删除无效的session对象，默认为true
        sessionManager.setDeleteInvalidSessions(true);
        // 是否开启定时调度器进行检测过期session，默认为true
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        //暂时设置为 5秒 用来测试
        sessionManager.setSessionValidationInterval(5000);

        // 取消url 后面的 JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 并发登录控制
     */
    @Bean
    public KickoutSessionControlFilter kickoutSessionControlFilter() {

        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        // 用于根据会话ID，获取会话进行踢出操作
        kickoutSessionControlFilter.setSessionManager(sessionManager());
        // 使用cacheManager获取响应的cache来缓存用户登录的会话，用于保存用户-会话之间的关系
        kickoutSessionControlFilter.setCacheManager(getEhCacheManager());
        // 是否踢出后来登录的，默认是false，即后者登录的用户踢出前者登录的用户
        kickoutSessionControlFilter.setKickoutAfter(false);
        // 同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两人登录
        kickoutSessionControlFilter.setMaxSession(1);
        // 被踢出后重定向到的地址
        kickoutSessionControlFilter.setKickoutUrl("/login?kickout=1");

        return kickoutSessionControlFilter;
    }
}

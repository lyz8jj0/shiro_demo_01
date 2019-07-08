package com.ray.shiro_demo_01.controller;

import com.ray.shiro_demo_01.entity.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by 李新宇
 * 2019-07-06 11:30
 */
@Controller
public class LoginController {
    /**
     * 访问项目根路径
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root(Model model) {
        Subject subject = SecurityUtils.getSubject();
        UserInfo user = (UserInfo) subject.getPrincipal();
        if (user == null) {
            return "redirect:/login";
        } else {
            return "redirect:/index";
        }
    }

    /**
     * 跳转到login页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        Subject subject = SecurityUtils.getSubject();
        UserInfo user = (UserInfo) subject.getPrincipal();
        if (user == null) {
            return "login.html";
        } else {
            return "redirect:index";
        }
    }

    /**
     * 用户登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginUser(HttpServletRequest request, String username, String password, Model model, HttpSession session) {

        //对密码进行加密
        //password=new SimpleHash("md5", password, ByteSource.Util.bytes(username.toLowerCase() + "shiro"),2).toHex();
        //如果有点击  记住我
        //UsernamePasswordToken usernamePasswordToken=new UsernamePasswordToken(username,password,remeberMe);

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();

        try {
            // 登录操作
            subject.login(usernamePasswordToken);
            UserInfo user = (UserInfo) subject.getPrincipal();
            // 更新用户登录时间，也可以在ShiroRealm里做
            session.setAttribute("user", user);
            model.addAttribute("user", user);
            return "index.html";
        } catch (Exception e) {
            //登录失败从request中获取shiro处理的异常信息 shiroLoginFailure:就是shiro异常类的全类名
            String exception = (String) request.getAttribute("shiroLoginFailure");
            model.addAttribute("msg", e.getMessage());
            //返回登录页面
            return "login";
        }
    }

    @RequestMapping("/index")
    public String index(HttpSession session, Model model) {
        Subject subject = SecurityUtils.getSubject();
        UserInfo user = (UserInfo) subject.getPrincipal();
        if (user == null) {
            return "login.html";
        } else {
            model.addAttribute("user", user);
            return "index.html";
        }
    }

    /**
     * 登出  这个方法没用到,用的是shiro默认的logout
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session, Model model) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        model.addAttribute("msg", "安全退出");
        return "login.html";
    }

    /**
     * 跳转到无权限页面
     */
    @RequestMapping("/unauthorized")
    public String unauthorized(HttpSession session, Model model) {
        return "unauthorized.html";
    }
}

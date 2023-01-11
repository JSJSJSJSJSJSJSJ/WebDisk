package com.joe.webdisk.controller;

import com.joe.webdisk.entity.FileStore;
import com.joe.webdisk.entity.User;
import com.joe.webdisk.utils.LogUtils;
import com.joe.webdisk.utils.MailUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

@Controller
public class LoginController extends BaseController {
    Logger logger = LogUtils.getInstance(LoginController.class);
    @PostMapping("/register")
    public String register(User user, String code, Map<String, Object> map) {
        String uCode = (String) session.getAttribute(user.getEmail()+"_code");
        if (!code.equals(uCode)) {
            map.put("errorMsg", "验证码错误");
            return "index";
        }
        user.setUserName(user.getUserName().trim());
        user.setImagePath("https://i.loli.net/2021/05/31/GJcmLonvkKyRFZD.png");
        user.setRegisterTime(new Date());
        if (userService.insert(user)) {
            FileStore store = FileStore.builder().userId(user.getUserId()).build();
            fileStoreService.addFileStore(store);
            user.setFileStoreId(store.getFileStoreId());
            userService.update(user);
            logger.info("注册用户成功！当前注册用户" + user);
            logger.info("注册仓库成功！当前注册仓库" + store);
        } else {
            map.put("errorMsg", "服务器发生错误，注册失败！");
            return "index";
        }
        session.removeAttribute(user.getEmail() + "_code");
        session.setAttribute("loginUser", user);
        return "redirect:/index";
    }

    @ResponseBody
    @RequestMapping("/sendCode")
    public String sendCode(String userName, String email, String password) {
        User userByEmail = userService.queryUserByEmail(email);
        if (userByEmail != null) {
            logger.error("发送验证码失败！邮箱已被注册！");
            return "exitEmail";
        }
        logger.info("开始发送邮件...\n" + "邮件发送对象为:" + mailSender);
        mailUtils = new MailUtils(mailSender);
        String code = mailUtils.sendCode(email, userName, password);
        session.setAttribute(email + "_code", code);
        return "success";
    }

    @PostMapping("/login")
    public String login(User user, Map<String, Object> map) {
        User userByEmail = userService.queryUserByEmail(user.getEmail());
        if (userByEmail == null) {
            map.put("errorMsg", "用户不存在，登录失败！");
            return "index";
        }

        User userByEmailAndPwd = userService.queryByEmailAndPwd(user.getEmail(), user.getPassword());
        if (userByEmailAndPwd == null) {
            map.put("errorMsg", "密码错误！");
            return "index";
        }

        session.setAttribute("loginUser", userByEmailAndPwd);
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout() {
        logger.info("用户退出登录！");
        session.invalidate();
        return "redirect:/";
    }

}

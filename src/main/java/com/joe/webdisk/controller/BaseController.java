package com.joe.webdisk.controller;

import com.joe.webdisk.entity.User;
import com.joe.webdisk.service.FileFolderService;
import com.joe.webdisk.service.FileStoreService;
import com.joe.webdisk.service.MyFileService;
import com.joe.webdisk.service.UserService;
import com.joe.webdisk.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BaseController {
    @Autowired
    protected UserService userService;
    @Autowired
    protected FileStoreService fileStoreService;
    @Autowired
    protected FileFolderService fileFolderService;
    @Autowired
    protected MyFileService myFileService;

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;
    protected User loginUser;

    @Autowired
    protected JavaMailSenderImpl mailSender;
    protected MailUtils mailUtils;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession(true);
        loginUser = (User) session.getAttribute("loginUser");
    }
}

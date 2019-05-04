package com.laqun.laqunserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.laqun.laqunserver.common.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@EnableAutoConfiguration
@RequestMapping("/api/webServer")
public class login {
    @RequestMapping("/login")
    private JSONObject login(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        log.info("start login");
        if(req.getParameter("loginPassword").equalsIgnoreCase(config.get("loginPassword"))){
            resJo.put("resInfo", "登录成功");
            HttpSession session = req.getSession();
            session.setAttribute("loginPassword", req.getParameter("loginPassword"));
            session.setMaxInactiveInterval(0);
        }else{
            resJo.put("resInfo", "密码错误");
        }
        return resJo;
    }

    @RequestMapping("/logout")
    private JSONObject logout(HttpServletRequest req){
        JSONObject resJo = new JSONObject();
        HttpSession session = req.getSession();
        session.removeAttribute("loginPassword");
        resJo.put("resInfo", "退出成功");
        return resJo;
    }

    @RequestMapping("/isLogin")
    public JSONObject isLogin(HttpServletRequest req) {
        HttpSession session = req.getSession();
        JSONObject resJo = new JSONObject();
        if (session.getAttribute("loginPassword") == null || !session.getAttribute("loginPassword").equals(config.get("loginPassword"))) {
            resJo.put("resInfo", "登录失败");
        } else {
            resJo.put("resInfo", "登录成功");
        }
        return resJo;
    }
}

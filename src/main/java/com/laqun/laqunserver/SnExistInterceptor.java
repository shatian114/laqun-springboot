package com.laqun.laqunserver;

import com.alibaba.fastjson.JSONObject;
import com.laqun.laqunserver.common.config;
import com.laqun.laqunserver.dao.SnRepository;
import com.laqun.laqunserver.entity.Sn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class SnExistInterceptor implements HandlerInterceptor {

    @javax.annotation.Resource
    private SnRepository snRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("检测sn是否存在拦截器开始执行");
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        JSONObject resJo = new JSONObject();
        try {
            Sn sn = snRepository.findBySn(request.getParameter("sn"));
            if(sn != null) {
                log.info("存在，继续往下执行");
                return true;
            }else{
                resJo.put("res", "fail");
                resJo.put("errInfo", "noSn" + request.getParameter("sn"));
                log.info("不存在，停止往下执行");
                response.getWriter().println(resJo);
                return false;
            }
        }catch (Exception e){
            log.info("不存在，停止往下执行");
            response.getWriter().println(resJo);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

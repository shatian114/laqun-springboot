package com.laqun.laqunserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    LoginInterceptor LoginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    SnExistInterceptor SnExistInterceptor() {
        return new SnExistInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(LoginInterceptor())
                .addPathPatterns("/api/webServer/**")
                .excludePathPatterns("/api/webServer/login", "/api/webSerber/logout", "/api/webServer/isLogin");
        registry.addInterceptor(SnExistInterceptor())
                .addPathPatterns("/api/phoneServer/**");
    }
}

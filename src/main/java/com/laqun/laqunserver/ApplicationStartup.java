package com.laqun.laqunserver;

import com.laqun.laqunserver.common.utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ResourceUtils;

@Slf4j
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            utils.webPath = ResourceUtils.getURL("").getPath();
            log.info("初始化完成，根路径为：" + utils.webPath);
        }catch (Exception e){
            log.error("获取根路径出错： " + e.getMessage());
        }

    }
}

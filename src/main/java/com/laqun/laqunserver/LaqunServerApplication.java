package com.laqun.laqunserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LaqunServerApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LaqunServerApplication.class);
        springApplication.addListeners(new ApplicationStartup());
        springApplication.run(args);
    }

}

package com.demo.allframework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author YUDI
 * @date
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.demo.allframework.mapper")
public class SummaryServiceApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SummaryServiceApplication.class);
        // app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}

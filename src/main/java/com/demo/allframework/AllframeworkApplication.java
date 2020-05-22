package com.demo.allframework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author CDY
 */
@SpringBootApplication
@MapperScan("com.demo.allframework.*.dao")
public class AllframeworkApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AllframeworkApplication.class);
        // app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}

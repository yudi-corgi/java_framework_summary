package com.demo.allframework.bareness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author YUDI-Corgi
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class BarenessApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarenessApplication.class, args);
    }

}

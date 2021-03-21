package com.demo.allframework.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author YUDI
 * @date 2020/12/6 16:25
 */
@SpringBootApplication
// @EnableDiscoveryClient
// @EnableHystrix
@EnableFeignClients(basePackages = {"com.demo.allframework.uaa.mapper"})
public class SummaryUAAServer {

    public static void main(String[] args) {
        SpringApplication.run(SummaryUAAServer.class,args);
    }

}

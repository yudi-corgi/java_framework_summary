package com.demo.allframework.bareness.controller;

import com.demo.allframework.bareness.config.I18nUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * i18n 配置测试控制器
 * @author YUDI-Corgi
 */
@RestController
@RequestMapping("/demo")
public class I18NController {

    @Resource
    private I18nUtil i18nUtil;

    @GetMapping("/{code}")
    public String cn(@PathVariable("code") String code) {
        return i18nUtil.getMessage(code, "默认消息");
    }

}

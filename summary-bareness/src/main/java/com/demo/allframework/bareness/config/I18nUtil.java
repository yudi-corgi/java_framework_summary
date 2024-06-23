package com.demo.allframework.bareness.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * i18n 工具类
 * @author YUDI-Corgi
 */
@Component
public class I18nUtil {

    @Value("${spring.messages.basename}")
    private String basename;

    private final CustomLocaleResolver localeResolver;

    // 国际化信息前缀
    private final String CODE_PREFIX = "CODE_";

    public I18nUtil(CustomLocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    /**
     * 获取国际化信息
     * @param code           信息编码
     * @param defaultMessage 默认内容
     * @return 国际化信息
     */
    public String getMessage(String code, String defaultMessage) {
        Locale locale = localeResolver.getLocal();
        return getMessage0(CODE_PREFIX + code, null, defaultMessage, locale);
    }

    /**
     * 获取国际化信息
     * @param code           信息编码
     * @param args           将为消息中的参数填充的参数数组
     * @param defaultMessage 默认消息
     * @param locale         语言环境
     * @return 国际化信息
     */
    private String getMessage0(String code, Object[] args, String defaultMessage, Locale locale) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        messageSource.setBasename(basename);
        String content;
        try {
            content = messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            System.out.println("国际化参数获取失败===>" + e);
            content = defaultMessage;
        }
        return content;

    }

}

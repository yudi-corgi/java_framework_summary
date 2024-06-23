package com.demo.allframework.bareness.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * LocaleResolver 是语言环境解析策略接口，实现它以自定义语言环境解析操作
 * @author YUDI-Corgi
 */
@Configuration
public class CustomLocaleResolver implements LocaleResolver {

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    /**
     * 通过解析请求来获取对应的语言环境
     * @return Locale
     */
    public Locale getLocal() {
        return resolveLocale(request);
    }

    /**
     * 通过给定的请求解析当前语言环境。在任何情况下都可以返回默认区域设置作为回退。
     * @param request the request to resolve the locale for
     * @return Locale
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {

        // 从请求头获取语言环境字符串
        String lang = request.getHeader("lang");
        // 默认是根据主机的语言环境来生成对象，如果无法获取，则默认 en
        Locale locale = Locale.getDefault();

        // 如果指定了语言环境
        if (StringUtils.hasText(lang)) {

            String[] info = lang.split("-");

            // 参数分别为语言、国家
            locale = new Locale(info[0], info[1]);

        }

        return locale;
    }

    /**
     * 用于实现 Locale 的切换，比如英文环境切换为中文，在 SessionLocaleResolver 和 CookieLocaleResolver 都有使用，可借鉴
     * @param request the request to be used for locale modification
     * @param response the response to be used for locale modification
     * @param locale the new locale, or {@code null} to clear the locale
     */
    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}

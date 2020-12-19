package com.demo.allframework.zuul.filter;

import com.demo.allframework.zuul.utils.Base64Util;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Zuul 过滤器
 * @author YUDI
 * @date 2020/12/18 23:17
 */
public class AuthFilter extends ZuulFilter {

    @Override
    public String filterType() {
        // 过滤器类型，pre:路由前执行，适合用于认证
        return "pre";
    }

    @Override
    public int filterOrder() {
        // 过滤器优先级，越大优先级越低
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器具体执行逻辑
     * @return
     * @throws ZuulException
     */
    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
        // 获取请求上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        // 从 SecurityContext 安全上下文获取令牌信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 令牌类型不属于 OAuth2Authentication 时，拒绝请求
        if(!(authentication instanceof OAuth2Authentication)){
            // currentContext.setSendZuulResponse(false);
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        // 获取 OAuth2Request 请求对象
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        Map<String,String> requestParams = oAuth2Request.getRequestParameters();
        Map<String, Object> jsonToken = new HashMap<>(requestParams);

        // 提取令牌中的用户信息
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        if(userAuthentication != null){
            // 保存用户基本信息
            jsonToken.put("principal",userAuthentication.getName());
            // 获取权限
            List<String> authorities = userAuthentication.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            jsonToken.put("authorities",authorities);
        }

        // 将 token 放在请求中并路由到微服务
        String json_token = new ObjectMapper().writeValueAsString(jsonToken);
        currentContext.addZuulRequestHeader("json-token", Base64Util.encodeUTF8StringBase64(json_token));
        return null;
    }
}

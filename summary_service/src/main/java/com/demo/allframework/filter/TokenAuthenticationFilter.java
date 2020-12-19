package com.demo.allframework.filter;

import com.demo.allframework.entity.SysUser;
import com.demo.allframework.utils.Base64Util;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下游服务通过该过滤器先获取网关转发请求是否包含 token
 * @author YUDI
 * @date 2020/12/19 0:01
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String base64JsonToken = request.getHeader("json-token");
        if(StringUtils.isNotEmpty(base64JsonToken)){
            // 获取 jsonToken 数据
            String jsonToken = Base64Util.decodeUTF8StringBase64(base64JsonToken);
            ObjectMapper mapper = new ObjectMapper();
            JavaType mapType = mapper.getTypeFactory().constructParametricType(HashMap.class, String.class,Object.class);
            Map<String,Object> params = mapper.readValue(jsonToken, mapType);
            // 用户信息及权限
            SysUser user = mapper.readValue(params.get("principal").toString(),SysUser.class);
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, String.class);
            List<String> authorities = (List<String>) params.get("authorities");

            // 新建并填充 authentication
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                    (user, null, AuthorityUtils.createAuthorityList(authorities.toArray(new String[0])));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 保存进 SecurityContext 安全上下文
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request,response);
    }
}

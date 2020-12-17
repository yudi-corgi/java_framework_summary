package com.demo.allframework.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 授权服务配置
 * @author YUDI
 * @date 2020/12/8 0:10
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter{

    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;
    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注册授权码授权类型服务实例，设置授权码存取方式
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource){
        // 采用内存方法存取
        // return new InMemoryAuthorizationCodeServices();
        // 采用数据库方式存取
        return new JdbcAuthorizationCodeServices(dataSource);
    }


    /**
     * 客户端详情服务配置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
        /*clients.inMemory()  // 使用内存存储
                .withClient("c1")  // 客户端ID:client_id
                .secret(new BCryptPasswordEncoder().encode("secret"))  // 客户端密钥：secret
                .resourceIds("res1")    // 资源列表
                // 当前客户端允许的授权类型
                .authorizedGrantTypes("authorization_code","password","client_credentials","implicit","refresh_token")
                .scopes("all")  // 授权范围
                .autoApprove(true)  // 授权码类型时 false 表示会跳转授权页面，true 则不跳转
                // 验证回调地址
                .redirectUris("http://www.baidu.com");*/
                // .and().withClient()  配置多个客户端详情可通过 and() 连接
    }

    /**
     * 配置客户端详情数据源和密码编码器
     * 将客户端信息存在数据库
     * @param dataSource
     * @return
     */
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource){
        JdbcClientDetailsService service = new JdbcClientDetailsService(dataSource);
        service.setPasswordEncoder(passwordEncoder);
        return service;
    }

    /**
     * 令牌端点访问安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                // oauth/token_key 的访问权限设置，提供公有密钥的端点（若使用的是 JWT Token）
                .tokenKeyAccess("permitAll()")
                // oauth/token_key 的访问权限设置，该端点用于资源服务解析令牌
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();  // 允许通过表单认证(申请令牌)
    }

    /**
     * 令牌访问端点和令牌服务配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                // 设置认证管理器实例
                .authenticationManager(authenticationManager)
                // 授权码授权类型服务
                .authorizationCodeServices(authorizationCodeServices)
                // 令牌服务
                .tokenServices(tokenServices())
                // 令牌端点的请求方式
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * 配置令牌服务
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        // 设置客户端详情服务
        services.setClientDetailsService(clientDetailsService);
        // 设置 token 持久化策略
        services.setTokenStore(tokenStore);
        // 令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        services.setTokenEnhancer(tokenEnhancerChain);
        // 令牌有效期
        services.setAccessTokenValiditySeconds(7200);
        // 开启令牌刷新
        services.setSupportRefreshToken(true);
        // 令牌刷新有效期
        services.setRefreshTokenValiditySeconds(259200);
        return services;
    }

}

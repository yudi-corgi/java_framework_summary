package com.demo.allframework.netty.websocket.example;

import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

/**
 * @author CDY
 * @date 2021/6/13
 * @description  引导服务器（加密）[不可用!!!!!]
 */
public class SecureChatServer extends ChatServer{

    private final SslContext sslContext;
    public SecureChatServer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected SecureChatServerInitializer createInitializer(
            ChannelGroup group) {
        return new SecureChatServerInitializer(group, sslContext);
    }

    @SneakyThrows
    public static void main(String[] args) {
        int port = 8089;
        SelfSignedCertificate certificate = new SelfSignedCertificate();
        SslContext sslContext = SslContext.newServerContext(certificate.certificate(),certificate.privateKey());
        final SecureChatServer endpoint = new SecureChatServer(sslContext);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(endpoint::destroy));
        future.channel().closeFuture().syncUninterruptibly();
    }

}



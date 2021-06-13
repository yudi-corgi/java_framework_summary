package com.demo.allframework.netty.websocket.example;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * @author CDY
 * @date 2021/6/13
 * @description  添加 SslHandler
 */
public class SecureChatServerInitializer extends ChatServerInitializer{

    private final SslContext sslContext;

    public SecureChatServerInitializer(ChannelGroup group, SslContext sslContext){
        super(group);
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        SSLEngine engine = sslContext.newEngine(ch.alloc());
        engine.setUseClientMode(false);
        ch.pipeline().addFirst(new SslHandler(engine));
    }
}

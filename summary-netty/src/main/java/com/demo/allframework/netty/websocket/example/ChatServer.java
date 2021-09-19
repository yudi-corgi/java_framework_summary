package com.demo.allframework.netty.websocket.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * @author CDY
 * @date 2021/6/12
 * @description  引导服务器（非加密）
 */
public class ChatServer {

    /**
     * 创建 ChannelGroup，其将保存所有已经连接的 WebSocket Channel
     */
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup eventGroup = new NioEventLoopGroup();
    private Channel channel;

    /**
     * 引导服务器
     * @param address  绑定的地址
     * @return
     */
    public ChannelFuture start(InetSocketAddress address){
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(eventGroup)
                // 添加子 Channel 属性，value 为 null 时该 AttributeKey 会删除
                .childAttr(AttributeKey.valueOf("attr"),"customer K-V")
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup));
        ChannelFuture future = boot.bind(address);
        // 阻塞直到绑定成功
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        return new ChatServerInitializer(group);
    }

    /**
     * 处理服务器关闭，释放所有资源
     */
    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        eventGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        int port = 8089;
        final ChatServer server = new ChatServer();
        ChannelFuture future = server.start(new InetSocketAddress(port));
        // 添加 shutdown 钩子
        Runtime.getRuntime().addShutdownHook(new Thread(server::destroy));
        // 阻塞直到关闭事件完成
        future.channel().closeFuture().syncUninterruptibly();
    }
}

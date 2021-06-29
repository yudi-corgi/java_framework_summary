package com.demo.allframework.netty.udpbroadcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;

/**
 * @author CDY
 * @date 2021/6/29
 * @description  LogEvent 监视器（客户端）
 */
public class LogEventMonitor {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LogEventDecoder());
                        pipeline.addLast(new LogEventHandler());
                    }
                }).localAddress(address);
    }

    /**
     * 绑定 Channel
     * @return
     */
    @SneakyThrows
    public Channel bind(){
        return bootstrap.bind().sync().channel();
    }

    /**
     * 关闭 EventLoopGroup
     */
    public void stop(){
        group.shutdownGracefully();
    }

    @SneakyThrows
    public static void main(String[] args) {
        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(0));
        try{
            Channel bind = monitor.bind();
            System.out.println("LogMonitor running...");
            bind.closeFuture().sync();
        }finally {
            monitor.stop();
        }
    }

}

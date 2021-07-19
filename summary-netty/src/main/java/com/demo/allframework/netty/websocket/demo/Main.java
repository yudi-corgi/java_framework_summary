package com.demo.allframework.netty.websocket.demo;

import com.demo.allframework.netty.handler.ShutdownHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import sun.misc.Signal;

/**
 * @author CDY
 * @date 2021/6/12
 * @description
 */
public class Main {

    private static String sig;
    static {
        // 根据系统获取信号量，win：SIGINT 接收 ctrl+c 中断命令  Linux：SIGTERM（等价于 kill pid 命令）
        sig = System.getProperties().getProperty("os.name").toLowerCase().startsWith("win") ? "INT" : "TERM";
        sig = "SIG" + sig;
    }

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebSocketChildChannelHandler());
            System.out.println("server running and waiting for client to connect.");
            Channel channel = bootstrap.bind(8888).sync().channel();
            channel.closeFuture().sync();
            // 注册信号处理器
            EventLoopGroup[] groups = { bossGroup, workGroup};
            ShutdownHandler shutdownHandler = new ShutdownHandler(groups);
            Signal signal = new Signal(sig);
            Signal.handle(signal,shutdownHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }/*finally {
            // 使用信号处理器，不在 finally 关闭资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }*/

        // 使用 JDK 的 shutdown 钩子函数关闭资源，当非守护线程全部结束时，该函数会被调用
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("ShutdownHook execute start...");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.out.println("ShutdownHook execute end...");
        }));
    }
}

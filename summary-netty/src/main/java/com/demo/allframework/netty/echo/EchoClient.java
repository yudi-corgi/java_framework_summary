package com.demo.allframework.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author CDY
 * @date 2021/5/29
 * @description  引导 Echo 客户端连接服务器远程地址并发送消息
 */
public class EchoClient {

    private final String host;
    private final int port;
    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        // if (args.length != 2){
        //     System.err.println("错误："+EchoClient.class.getSimpleName()+" <host> <port>");
        //     return;
        // }
        // String host = args[0];
        // int port = Integer.parseInt(args[1]);
        new EchoClient("127.0.0.1",9999).start();
    }

    public void start() throws Exception{
        // 1. 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            // 2. 创建 Bootstrap，引导 Channel 供客户端使用
            Bootstrap b = new Bootstrap();
            // 2.1  指定 EventLoopGroup 来处理客户端事件，需要使用于 NIO 的实现
            b.group(group)
                    // 2.2 创建适用于 NIO 传输的 Channel 类型
                    .channel(NioSocketChannel.class)
                    // 2.3 设置服务器的 Host、Port
                    .remoteAddress(new InetSocketAddress(host,port))
                    // 2.4 在创建 Channel 时为其 ChannelPipeline 添加一个 EchoClientHandler 实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            // 3. 阻塞连接直到完成
            ChannelFuture cf = b.connect().sync();
            // 4. 阻塞到 Channel 关闭并返回 CloseFuture（ChannelFuture 子类）
            cf.channel().closeFuture().sync();
        }finally {
            // 关闭线程池并释放所有资源
            group.shutdownGracefully();
        }
    }
}

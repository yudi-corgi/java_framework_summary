package com.demo.allframework.netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author CDY
 * @date 2021/5/29
 * @description 引导服务器，绑定到服务器并监听传入连接请求的端口，并配置 Channel 将入站消息传递给 EchoServerChannel 实例
 */
public class EchoServer {

    private final int port;
    public EchoServer(int port){
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        // if(args.length != 1){
        //     System.err.println("错误：" + EchoServer.class.getSimpleName() + " <port>");
        //     return;
        // }
        // int port = Integer.parseInt(args[0]);
        int port = Integer.parseInt("9999");
        new EchoServer(port).start();
    }

    /**
     * 绑定服务器并监听端口，配置 Channel
     * @throws Exception 异常信息
     */
    public void start() throws Exception{
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 1. 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            // 2. 创建 ServerBootstrap，用于引导 ServerChannel
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group)
                    // 2.1 因指定了 NioEventLoopGroup，所以使用的 Channel 类型为 NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 2.2 指定端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    // 2.3 当一个新的连接被接受时，会创建一个新的子 Channel，添加一个 EchoServerHandler 到子 Channel 的 ChannelPipeline 进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            // 3. 异步绑定服务器，但调用 sync() 后阻塞等待到绑定完成
            ChannelFuture cf = sb.bind().sync();
            Channel c = cf.channel();
            // 4. 当关闭 Channel 时返回 CloseFuture(ChannelFuture子类)
            // 调用 sync() 后阻塞直到返回，即必须等到 Channel.close() 才完成
            cf.channel().closeFuture().sync();
        }finally {
            // 关闭 EventLoopGroup 并释放所有资源
            group.shutdownGracefully().sync();
        }
    }
}

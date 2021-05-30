package com.demo.allframework.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author CDY
 * @date 2021/5/29
 * @description EchoServer ChannelHandler，接收客户端数据并处理
 */
@ChannelHandler.Sharable //标示一个 ChannelHandler 可以被多个 Channel(ChannelPipeline) 安全地共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当 Channel 读取到消息时调用
     * @param ctx  Channel 处理器上下文
     * @param msg  读取的消息
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("服务器接收：" + in.toString(StandardCharsets.UTF_8));
        // 将接收的消息写给发送者，而不冲刷出站消息
       ctx.write(msg);
    }

    /**
     * 当当前读取操作读取的最后一条消息已被 channelRead(ChannelHandlerContext, Object) 消耗时调用
     * @param ctx  Channel 处理器上下文
     * @throws Exception 异常
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将未决数据冲刷到远程节点，并且关闭该 Channel（此处添加了内置的 ChannelFutureListener）
        // 未决数据（pending message）：指目前暂存域 ChannelOutboundBuffer 中的消息，在下一次调用 flush() 或 writeAndFlush() 时将会尝试写出到套接字
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 针对异常情况的回调方法
     * @param ctx  Channel 处理器上下文
     * @param cause  异常对象
     * @throws Exception  异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

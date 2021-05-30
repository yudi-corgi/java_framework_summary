package com.demo.allframework.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * @author CDY
 * @date 2021/5/29
 * @description  客户端 ChannelHandler 发送与接收服务器消息，并且指定处理的消息的类型
 */
@ChannelHandler.Sharable  //标示一个 ChannelHandler 可以被多个 Channel(ChannelPipeline) 安全地共享
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 发送消息到服务器
     * @param ctx  Channel 处理器上下文
     * @throws Exception  异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", StandardCharsets.UTF_8).retain());
    }

    /**
     * 接收服务器信息
     * @param ctx  Channel 处理器上下文
     * @param msg  读取的消息
     * @throws Exception  异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // 这里不需要调用 flush 相关操作是因为 SimpleChannelInboundHandler 已经负责将保存该消息的 ByteBuf 的内存引用给释放了
        System.out.println("客户端接收："+ msg.toString(StandardCharsets.UTF_8));
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

package com.demo.allframework.netty.udpbroadcast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author CDY
 * @date 2021/6/29
 * @description  LogEvent 处理器，这里只是简单输出到控制台
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent event) throws Exception {
        String builder = event.getReceivedTimestamp() +
                " [" +
                event.getSource().toString() +
                "] [" +
                event.getLogfile() +
                "] : " +
                event.getMsg();
        System.out.println(builder);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

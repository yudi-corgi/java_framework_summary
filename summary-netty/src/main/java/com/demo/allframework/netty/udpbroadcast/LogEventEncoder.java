package com.demo.allframework.netty.udpbroadcast;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author CDY
 * @date 2021/6/18
 * @description  编码器，将 LogEvent 编码为 DatagramPacket
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;
    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent logEvent, List<Object> out) throws Exception {
        byte[] file = logEvent.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = ctx.alloc().buffer(file.length + msg.length + 1);
        // 将文件名写入 buf
        buf.writeBytes(file);
        // 添加分隔符
        buf.writeByte(LogEvent.SEPARATOR);
        // 写入日志消息
        buf.writeBytes(msg);
        // 将一个拥有数据和目的地地址的新 DatagramPacket 添加到出站的消息列表中
        System.out.println(remoteAddress.getAddress() + ":" + remoteAddress.getPort());
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}

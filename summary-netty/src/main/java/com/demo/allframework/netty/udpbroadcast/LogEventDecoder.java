package com.demo.allframework.netty.udpbroadcast;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @author CDY
 * @date 2021/6/29
 * @description  数据报解码器
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        // 获取数据报中的 ByteBuf
        ByteBuf data = datagramPacket.content();
        System.out.println("数据发送者："+datagramPacket.sender());
        System.out.println("数据接收者："+datagramPacket.recipient());
        // 根据分隔符获取文件名
        int idx = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String filename = data.slice(0, idx).toString(CharsetUtil.UTF_8);
        // 剩余的即为传输的文本数据
        String logMsg = data.slice(idx + 1,data.readableBytes()).toString(CharsetUtil.UTF_8);
        LogEvent event = new LogEvent(datagramPacket.sender(),System.currentTimeMillis(), filename, logMsg);
        // 添加解码的 LogEvent 到列表中
        out.add(event);
    }
}

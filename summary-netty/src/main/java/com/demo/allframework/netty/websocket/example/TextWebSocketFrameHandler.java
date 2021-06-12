package com.demo.allframework.netty.websocket.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @author CDY
 * @date 2021/6/12
 * @description 文本帧处理器
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;
    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 检查事件是否为握手完成，若完成，通道升级为 WebSocket
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // 若握手成功则删除 HTTP 处理器，因为协议更变为 WebSocket，不会再接受 HTTP 请求
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 通知所有已经连接的 WebSocket 客户端新的客户端已连接
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            // 将新的 WebSocket 通道写到 group 以便接收所有消息
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 增加消息的引用计数，并写到 ChannelGroup 中所有已经连接的客户端
        group.writeAndFlush(msg.retain());
    }
}

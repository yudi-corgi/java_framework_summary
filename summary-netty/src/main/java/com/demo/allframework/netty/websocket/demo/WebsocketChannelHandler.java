package com.demo.allframework.netty.websocket.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * @author CDY
 * @date 2021/6/12
 * @description
 */
public class WebsocketChannelHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private static final String WEB_SOCKET_URL ="ws://127.0.0.1:8888/websocket";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpMessage) {
            handleHttpRequest(ctx,(FullHttpRequest)msg);
        }else if(msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx,(WebSocketFrame)msg);
        }
    }

    /**
     * 处理 WebSocketFrame
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if(frame instanceof CloseWebSocketFrame) {
            // 关闭握手操作
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
        }

        if(frame instanceof PingWebSocketFrame) {
            // Ping 帧处理，响应 Pong 帧
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if(!(frame instanceof TextWebSocketFrame)) {
            System.out.println("暂不支持二进制消息。");
            throw new RuntimeException("[" + this.getClass().getName() + "]不支持消息");
        }
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("服务端接收到消息 ==>" + request);
        TextWebSocketFrame res = new TextWebSocketFrame(new Date().toString() + " - " + ctx.channel().id() + " ===>" + request);
        // 将消息写到 ChannelGroup 中的所有 Channel
        NettyConfig.group.writeAndFlush(res);
    }

    /**
     * 处理 HTTP 请求
     * @param ctx
     * @param request
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        final DecoderResult decoderResult = request.decoderResult();
        System.out.println(decoderResult.isSuccess());
        if(!request.decoderResult().isSuccess()|| !"websocket".equals(request.headers().get("Upgrade"))) {
            // 非协议升级时响应 Bad Request
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 创建 WebSocketServerHandshaker 对象
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
        handshaker = factory.newHandshaker(request);
        if(handshaker==null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            // 执行打开握手（open）
            handshaker.handshake(ctx.channel(), request);
        }

    }

    /**
     * HTTP 请求响应
     * @param ctx
     * @param request
     * @param response
     */
    private void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest request,DefaultFullHttpResponse response) {
        if (response.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if (response.status().code() != HttpResponseStatus.OK.code()) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端建立连接");
        NettyConfig.group.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端断开连接");
        NettyConfig.group.remove(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}

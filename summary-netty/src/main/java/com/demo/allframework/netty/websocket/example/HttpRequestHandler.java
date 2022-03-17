package com.demo.allframework.netty.websocket.example;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author CDY
 * @date 2021/6/12
 * @description Http 请求处理器
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static final File INDEX;

    static {
        // 获取类的根路径
        URL location = HttpRequestHandler.class.getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI().getPath() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
            System.out.println(INDEX.getAbsolutePath());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 如果请求了 WebSocket 协议升级，则增加引用计数，并传递给下一个 ChannelInboundHandler
        if (wsUri.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            // 读取 index.html
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            System.out.println(request.protocolVersion());
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(request);

            // 若请求了 keepAlive 则添加所需要的 HTTP 头信息
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }

            // 将 HttpResponse 写到客户端
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {
                // 使用 FileRegion 将 index.html 写到客户端
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, INDEX.length()));
            } else {
                // 若是加密，使用 ChunkedNioFile（实现了 ChunkedInput）来获取数据并写到客户端
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            // 写 LastHttpContent 并冲刷至客户端
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            // 若无请求 keepAlive，则写操作完成后关闭 Channel
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 处理 100 Continue 请求以符合 HTTP1.1 规范
     *
     * @param ctx ChannelHandler 上下文
     */
    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    /**
     * 异常处理，关闭 Channel
     *
     * @param ctx   ChannelHandler 上下文
     * @param cause 异常信息
     * @throws Exception exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

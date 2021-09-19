package com.demo.allframework.netty.websocket.demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author CDY
 * @date 2021/6/12
 * @description
 */
public class WebSocketChildChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("http-codec",new HttpServerCodec());
        ch.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
        // ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        ch.pipeline().addLast("handler",new WebsocketChannelHandler());
        // ch.pipeline().addLast(new Default)
    }
}

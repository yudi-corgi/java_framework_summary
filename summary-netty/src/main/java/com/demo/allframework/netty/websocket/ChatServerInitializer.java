package com.demo.allframework.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author CDY
 * @date 2021/6/12
 * @description  Channel 初始化类
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;
    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 获取管道
        ChannelPipeline pipeline = ch.pipeline();
        // 服务端 HTTP 编解码器
        pipeline.addLast(new HttpServerCodec());
        // 文件内容写入，主要是读取 ChunkedNioFile（ChunkedInput） 封装的数据
        pipeline.addLast(new ChunkedWriteHandler());
        // HTTP 数据聚合器，聚合后数据最大为 64*1024，用于聚合 HttpMessage 和 HttpContent
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        // HTTP 处理器
        pipeline.addLast(new HttpRequestHandler("/ws"));
        // 该处理器处理 WebSocket 升级握手，Close、Ping、Pong 控制帧
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 文本帧处理器
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }
}

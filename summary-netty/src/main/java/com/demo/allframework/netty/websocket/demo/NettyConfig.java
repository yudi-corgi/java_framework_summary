package com.demo.allframework.netty.websocket.demo;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author CDY
 * @date 2021/6/12
 * @description
 */
public class NettyConfig {

    /**
     * 创建 ChannelGroup 保存所有的客户端 Channel，GlobalEventExecutor 是只含一个线程的单例事件执行对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}

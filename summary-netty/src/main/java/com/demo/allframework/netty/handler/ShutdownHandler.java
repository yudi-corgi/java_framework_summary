package com.demo.allframework.netty.handler;

import io.netty.channel.EventLoopGroup;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Arrays;

/**
 * @author CDY
 * @date 2021/7/18
 * @description  监听信号量，优雅关闭 netty
 */
public class ShutdownHandler implements SignalHandler {

    private EventLoopGroup[] group;

    public ShutdownHandler(EventLoopGroup[] group) {
        this.group = group;
    }

    @Override
    public void handle(Signal signal) {
        // 获取信号
        String sig = signal.getName();
        // SIGINT：中断信号，执行关闭 netty
        if("SIGINT".equals(sig)){
            Arrays.stream(group).forEach(EventLoopGroup::shutdownGracefully);
        }
    }
}

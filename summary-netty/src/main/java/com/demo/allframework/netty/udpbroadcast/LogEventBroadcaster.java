package com.demo.allframework.netty.udpbroadcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * @author CDY
 * @date 2021/6/18
 * @description
 */
public class LogEventBroadcaster {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        // 引导 NioDatagramChannel 无连接
        bootstrap.group(group).channel(NioDatagramChannel.class)
                // 设置 SO_BROADCAST 套接字选项：广播模式，
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws Exception {
        // 绑定 0 端口，将消息定向到本地网络的主机，并获取 Channel
        Channel ch = bootstrap.bind(0).sync().channel();
        long pointer = 0;
        // 不断循环读取文件，一旦到尾部则休眠一秒后再重复操作
        for (;;) {
            long len = file.length();
            if (len < pointer) {
                // 如果有必要，将文件指针设置到该文件的最后一个字节
                pointer = len;
            } else if (len > pointer) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                // 设置当前的文件指针，以确保没有任何的旧日志被发送
                raf.seek(pointer);
                String line;
                while ((line = raf.readLine()) != null) {
                    // 对于每个日志条目，写入一个 LogEvent 到 Channel 中
                    ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                // 移动指针，存储文件中的当前位置
                pointer = raf.getFilePointer();
                raf.close();
            }
            try {
                // 休眠 1 秒，如果被中断，则退出循环；否则重新处理它
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    /**
     * 关闭资源
      */
    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        // 255.255.255.255 表示本地(局域网)广播
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", 9999), new File("D:\\Config.ini"));
        try {
            broadcaster.run();
        } finally {
            broadcaster.stop();
        }
    }

}

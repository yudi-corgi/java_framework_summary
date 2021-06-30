package com.demo.allframework.netty.udpbroadcast;

import java.net.InetSocketAddress;

/**
 * @author CDY
 * @date 2021/6/17
 * @description  日志消息对象
 */
public class LogEvent {

    public static final byte SEPARATOR = (byte) '>';
    /**
     * 目标地址
     */
    private final InetSocketAddress source;
    /**
     * 发送的日志文件名称
     */
    private final String logfile;
    /**
     * 消息内容
     */
    private final String msg;
    /**
     * 接收消息的时间戳
     */
    private final long received;

    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received,
                    String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {
        return source;
    }
    public String getLogfile() {
        return logfile;
    }
    public String getMsg() {
        return msg;
    }
    public long getReceivedTimestamp() {
        return received;
    }

}

package com.demo.allframework.rabbitmq.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 获取连接的通用工具
 * @author YUDI
 * @date 2020/12/27 18:09
 */
public class CommonUtil {

    private static ConnectionFactory factory;
    static {
        // 创建连接工厂对象，指定 IP、Port 连接 MQ Server，也可以直接指定主机名称进行绑定
        factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        // factory.setHost("localhost");

        // 指定虚拟主机 VirtualHost、VirtualHost 对应的访问账户密码
        // 虚拟主机及访问用户可在 Web 管理平台/命令行进行配置
        factory.setVirtualHost("/ems");
        factory.setUsername("ems");
        factory.setPassword("123");
    }

    /**
     * 创建 MQ Server 连接
     * @return
     */
    public static Connection getConnection(){
        try{
            return factory.newConnection();
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭连接和通道
     * @param connection
     * @param channel
     */
    public static void closeConnectionAndChannel(Connection connection, Channel channel){
        try {
            if (channel != null) {
                channel.close();
            }
            if(connection != null){
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}

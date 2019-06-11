package com.tamakiakoo.demo1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tamakiakoo.Utils.ConnectionUtil;

import java.io.IOException;

public class Provider {
    public static void main(String[] args) throws IOException {
        //1、连接Rabbitmq
        Connection connection = ConnectionUtil.getConnection();

        //2、通过连接获得通道对象，后续的所有操作，都是通过通道对象操作的
        Channel channel = connection.createChannel();

        //3、通过通道创建一个队列
        channel.queueDeclare("myqueue", false, false,false, null);

        //给队列发送消息
        for (int i = 0; i < 10; i++) {
            String msg = "Hello RabbitMQ!" + i;
            channel.basicPublish("", "myqueue", null, msg.getBytes("utf-8"));
        }

        connection.close();

        System.out.println("当前提供者以执行完成~~~~~~~~");
    }
}

package com.tamakiakoo.demo1;

import com.rabbitmq.client.*;
import com.tamakiakoo.Utils.ConnectionUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Consumer {
    public static void main(String[] args) throws IOException {

        //创建一个连接池
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        //1、连接Rabbitmq
        Connection connection = ConnectionUtil.getConnection();

        //2、通过连接获得通道对象，后续的所有操作，都是通过通道对象操作的
        Channel channel = connection.createChannel();

        //3、通过通道创建一个队列
        channel.queueDeclare("myqueue", false, false, false, null);

        channel.basicConsume("myqueue", true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String str = new String(body, "utf-8");
                            System.out.println("接收到队列中的消息：" + str);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }
}

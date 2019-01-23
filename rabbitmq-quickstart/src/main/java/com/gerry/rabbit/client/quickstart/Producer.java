package com.gerry.rabbit.client.quickstart;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者
 * @author Gerry_Pang
 */
public class Producer {

	public static void main(String[] args) {
		// 1 创建一个ConnecitonFactory，并进行配置
		ConnectionFactory connectionFacotory = new ConnectionFactory();
		connectionFacotory.setHost("127.0.0.1");
		connectionFacotory.setPort(5672);
		connectionFacotory.setVirtualHost("/");

		String exchangName = "test001";
		String message = "Hello world !!!";
		try {
			// 2 通过连接工厂创建Connection
			Connection connection = connectionFacotory.newConnection();
			// 3 通过connection创建Channel
			Channel channel = connection.createChannel();
			// 4 通过channel发送消息
			for (int i = 0; i < 5; i++) {
				// 重点： 当exchangge为空时，消息会发生到和路由key一样的queue中
				channel.basicPublish("", exchangName, null, message.getBytes());
				System.out.println("生产端发送第[" + i + "]条：" + message);
			}
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

}

package com.gerry.pang.rabbitmq.requeue;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		
		Channel channel = connection.createChannel();
		
		// 指定消息投递模式： 消息确认模式
		channel.confirmSelect();
		
		String message = "消息重回队列";
		String exchangeName = "test.exchange.requeue";
		String routingKeyName = "test.123";
		
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, routingKeyName, false, false, null, message.getBytes());
			System.out.println("发送消息： " + message);
		}
		
		// 消息确认模式监听
		channel.addConfirmListener(new ConfirmListener() {
			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("======== nack =========");
			}
			
			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("======== ack =========");
			}
		});
		
		channel.close();
		connection.close();
	}
}

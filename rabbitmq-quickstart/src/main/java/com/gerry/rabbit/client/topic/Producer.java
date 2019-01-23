package com.gerry.rabbit.client.topic;

import com.rabbitmq.client.Channel;
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
		
		String exchangeName = "test.exchange.topic";
		String routingKeyName1 = "test.123";
		String routingKeyName2 = "test.123.123";
		String message1 = "send message1 topic type";
		String message2 = "send message2 topic type";
	
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, routingKeyName1, null, message1.getBytes());
			System.out.println("producer1 :" + message1);
		}
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, routingKeyName2, null, message2.getBytes());
			System.out.println("producer2 :" + message2);
		}
		
		
	}
}

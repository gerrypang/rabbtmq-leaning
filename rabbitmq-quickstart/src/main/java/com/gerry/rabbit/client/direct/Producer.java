package com.gerry.rabbit.client.direct;

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
		
		String exchangeName = "test.exchange.direct";
		String routingKeyName = "test.123";
		String message = "send message direct type";
	
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, routingKeyName, null, message.getBytes());
			System.out.println("producer :" + message);
		}
		
	}
}

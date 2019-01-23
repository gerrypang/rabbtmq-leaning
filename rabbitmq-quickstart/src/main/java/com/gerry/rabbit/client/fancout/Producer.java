package com.gerry.rabbit.client.fancout;

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
		
		String exchangeName = "test.exchange.fanout";
		String message = "send message fanout type";
	
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, "", null, message.getBytes());
			System.out.println("producer :" + message);
		}
		
	}
}

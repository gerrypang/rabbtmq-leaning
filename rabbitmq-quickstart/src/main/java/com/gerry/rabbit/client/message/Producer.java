package com.gerry.rabbit.client.message;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
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
		String message = "send message topic type";
		
		
		Map<String,Object> headers = new HashMap<>();
		headers.put("h", "123");
		headers.put("q", "abc");
		// 税则消息属性
		AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
				.deliveryMode(2)
				.contentEncoding("UTF-8")
				.expiration("10000")
				.headers(headers)
				.build();
	
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, routingKeyName1, properties, message.getBytes());
			System.out.println("producer :" + message);
		}
		
		
	}
}

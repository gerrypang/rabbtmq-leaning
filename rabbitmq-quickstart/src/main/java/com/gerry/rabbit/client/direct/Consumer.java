package com.gerry.rabbit.client.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class Consumer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		String queueName = "test.queue.direct";
		String routingKey = "test.123";
		String exchangeName = "test.exchange.direct";
		channel.exchangeDeclare(exchangeName, "direct", true, false, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey, null);
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);
		
		while(true) {
			Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			String exchange = delivery.getEnvelope().getExchange();
			System.out.println("========= comsumer ===========");
			System.out.println("receive message: "+ message);
			System.out.println("exchange : "+ exchange.toString());
		}
		
		
	}
	
	
}

package com.gerry.rabbit.client.fancout;

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
		String queueName1 = "test.queue.fanout1";
		String queueName2 = "test.queue.fanout2";
		String queueName3 = "test.queue.fanout3";
		String routingKey = "";
		String exchangeName = "test.exchange.fanout";
		channel.exchangeDeclare(exchangeName, "fanout", true, false, false, null);
		channel.queueDeclare(queueName1, true, false, false, null);
		channel.queueBind(queueName1, exchangeName, routingKey, null);
		
		channel.queueDeclare(queueName2, true, false, false, null);
		channel.queueBind(queueName2, exchangeName, routingKey, null);
		
		channel.queueDeclare(queueName3, true, false, false, null);
		channel.queueBind(queueName3, exchangeName, routingKey, null);
		
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName1, true, consumer);
		
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

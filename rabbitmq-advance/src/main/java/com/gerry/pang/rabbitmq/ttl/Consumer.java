package com.gerry.pang.rabbitmq.ttl;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Consumer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		
		Connection connection = connectionFactory.newConnection();
		
		Channel channel = connection.createChannel();
		
		String exchangeName = "test.exchange.ttl";
		String routingKeyName = "test.#";
		String queueName = "test.queue.ttl";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, false, null);
		// queue�г�ʱ����
		Map<String,Object> queueArguments = new HashMap<>();
		queueArguments.put("x-message-ttl", 30000);
		channel.queueDeclare(queueName, true, false, false, queueArguments);
		channel.queueBind(queueName, exchangeName, routingKeyName, null);
		// ������ʽ
		channel.basicQos(0, 1, false);
		// ����ʱ��auto-ack����Ϊfalse
		channel.basicConsume(queueName, false, new CustomConsumer(channel));
	}
}

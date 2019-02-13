package com.gerry.pang.rabbitmq.ttl;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
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
		
		// ָ����ϢͶ��ģʽ�� ��Ϣȷ��ģʽ
		channel.confirmSelect();
		
		String message = "��Ϣ��ʱttl";
		String exchangeName = "test.exchange.ttl";
		String routingKeyName = "test.123";
		
		AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
				.deliveryMode(2)
				.contentEncoding("UTF-8")
				// ��Ϣ��ʱ
				.expiration("5000")
				.build();
		
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, routingKeyName, false, false, properties, message.getBytes());
			System.out.println("������Ϣ�� " + message);
		}
		
		// ��Ϣȷ��ģʽ����
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

		Thread.sleep(50000);
		
		channel.close();
		connection.close();
	}
}

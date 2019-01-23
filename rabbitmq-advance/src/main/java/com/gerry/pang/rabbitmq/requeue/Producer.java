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
		
		// ָ����ϢͶ��ģʽ�� ��Ϣȷ��ģʽ
		channel.confirmSelect();
		
		String message = "��Ϣ�ػض���";
		String exchangeName = "test.exchange.requeue";
		String routingKeyName = "test.123";
		
		for (int i = 0; i < 5; i++) {
			channel.basicPublish(exchangeName, routingKeyName, false, false, null, message.getBytes());
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
		
		channel.close();
		connection.close();
	}
}

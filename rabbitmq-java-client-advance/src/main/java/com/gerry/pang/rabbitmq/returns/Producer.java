package com.gerry.pang.rabbitmq.returns;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;

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
		
		String message = "消息回退模式";
		String exchangeName = "test.exchange.return";
		String routingKeyName = "aaa.123";
		
		channel.addReturnListener(new ReturnListener() {
			@Override
			public void handleReturn(int replyCode, String replyText, String exchange, 
					String routingKey, BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println("========= handle return ==========");
				System.out.println("return code: "+ replyCode);
				System.out.println("return text: "+ replyText);
				System.out.println("message: "+new String(body));
			}
		});
		
		channel.basicPublish(exchangeName, routingKeyName, true, false, null, message.getBytes());
		System.out.println("发送消息： " + message);
		
//		// 消息确认模式监听
//		channel.addConfirmListener(new ConfirmListener() {
//			@Override
//			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
//				System.out.println("======== nack =========");
//			}
//			
//			@Override
//			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
//				System.out.println("======== ack =========");
//			}
//		});

		Thread.sleep(500000);
		
		channel.close();
		connection.close();
	}
}

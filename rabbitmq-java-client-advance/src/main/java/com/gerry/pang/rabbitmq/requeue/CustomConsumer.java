package com.gerry.pang.rabbitmq.requeue;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class CustomConsumer extends DefaultConsumer {

	private Channel channel;
	 
	public CustomConsumer(Channel channel) {
		super(channel);
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
			throws IOException {
		System.out.println("======== custom consumer ==========");
		System.out.println("consumerTag :" + consumerTag);
		System.out.println("envelope :" + envelope);
		System.out.println("properties :" + properties);
		System.out.println("body :" + new String(body));
		long deliveryTag = envelope.getDeliveryTag();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(deliveryTag % 2 == 0) {
			channel.basicAck(deliveryTag, false);
			System.out.println("=== 消息签收 ====");
		} else {
			channel.basicNack(deliveryTag, false, true);
			System.out.println("=== 消息重回队列 ====");
			
		}
	}
	
	



}

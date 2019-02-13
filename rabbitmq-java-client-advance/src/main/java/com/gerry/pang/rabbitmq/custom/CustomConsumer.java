package com.gerry.pang.rabbitmq.custom;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class CustomConsumer extends DefaultConsumer {

	public CustomConsumer(Channel channel) {
		super(channel);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
			throws IOException {
		System.out.println("======== custom consumer ==========");
		System.out.println("consumerTag :" + consumerTag);
		System.out.println("envelope :" + envelope);
		System.out.println("properties :" + properties);
		System.out.println("body :" + new String(body));
	}



}

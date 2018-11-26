package com.gerry.pang.rabbitmq.woker;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class SimpleConsumer extends DefaultConsumer {

	public SimpleConsumer(Channel channel) {
		super(channel);
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, 
			BasicProperties properties, byte[] body) throws IOException {
		System.out.println(consumerTag);
		System.out.println("===== 收到消息了===== ");
		System.out.println("消息属性为：" + properties);
		System.out.println("消息内容为：" + new String(body));
		long deliveryTag = envelope.getDeliveryTag();
		// 消息接收手工确认
		getChannel().basicAck(deliveryTag, true);
	}

	
}

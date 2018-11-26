package com.gerry.pang.rabbitmq.woker;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.BasicProperties;

public class SimpleProducter {
	
	private byte[] createMessage(String tag) {
		// 设置消息体
		String demo = "Hello, world! " + tag ;
		byte[] messageBodyBytes = demo.getBytes();
		return messageBodyBytes;
	}
	
	public void sendMessage(Channel channel) {
		// 设置消息头
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("latitude",  51.5252949);
		headers.put("longitude", -0.0905493);
		
		// 设置消息参数
		BasicProperties properties = new AMQP.BasicProperties.Builder()
				.headers(headers) // 消息头
				.contentType("text/plain") // 传输协议
				.deliveryMode(2) // 消息是否持久化 1-否；2-是
				.priority(1) // 优先级
				.appId("rabbitmq-java-cient") // 应用id
				.messageId(System.currentTimeMillis() + "") // 消息的id
				.contentEncoding("UTF-8") // 编码方式
				.timestamp(new Date()) // 消息的时间戳
				.build();
		
		try {
			channel.basicPublish("EX.client.topic.demo", "com.abc.1", properties, createMessage("topic.1"));
			channel.basicPublish("EX.client.topic.demo", "com.abc.2", properties, createMessage("topic.2"));
			channel.basicPublish("EX.client.topic.demo", "com.abc.3", properties, createMessage("topic.3"));
			
			channel.basicPublish("EX.client.direct.demo", "com.123", properties, createMessage("direct.1"));
			channel.basicPublish("EX.client.direct.demo", "com.123", properties, createMessage("direct.2"));
			channel.basicPublish("EX.client.direct.demo", "com.123", properties, createMessage("direct.3"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

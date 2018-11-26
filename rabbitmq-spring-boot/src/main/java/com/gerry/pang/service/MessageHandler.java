package com.gerry.pang.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.rabbitmq.client.Channel;

public class MessageHandler {
	
//	public void handleMessage(byte[] message) {
//		System.out.println("---------handleMessage-------------");
//		System.out.println(new String(message));
//	}
	
	public void handleTopicMessage(Map message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
		System.out.println("---------handleTopicMessage-------------");
		System.out.println(message.toString());
		try {
			channel.basicAck(tag, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("消息已经确认");
	}
	
	public void handleDirectMessage(Map message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
		System.out.println("---------handleDirectMessage-------------");
		System.out.println(message.toString());
		try {
			channel.basicAck(tag, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("消息已经确认");
	}

}

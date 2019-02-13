package com.gerry.pang.producer;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.gerry.pang.entity.User;

@Component
public class Sender {
	
	@Autowired
	private RabbitTemplate rabbitTemplate; 
	
	final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			System.out.println("======== confirm callback========");
			System.out.println("ack:" + ack);
			System.out.println("correlationData:" + correlationData);
			if (!ack) {
				System.err.println("cause:" + cause);
			}
		}
	};
	
	final ReturnCallback returnCallback = new ReturnCallback() {
		@Override
		public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
				String exchange, String routingKey) {
			System.out.println("======== return callback========");
			System.out.println("message:"+ message);
			System.out.println("replyCode:"+ replyCode);
			System.out.println("replyText:"+replyText);
			System.out.println("exchange:"+ exchange);
			System.out.println("routingKey:"+ routingKey);
		}
	};
	
	/**
	 * 发送消息
	 * @param message 消息对象
	 * @param properties 消息头属性
	 */
	public void sendMessage(Object message, Map<String,Object> properties) {
		MessageHeaders messageHeaders = new MessageHeaders(properties);
		Message sendMessage = MessageBuilder.createMessage(message, messageHeaders);
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		// 消息唯一标识
		String id = UUID.randomUUID().toString();
		CorrelationData correlationData = new CorrelationData(id);
		rabbitTemplate.convertAndSend("com.gerry.springboot.exchange", "springboot.123", sendMessage, correlationData);
	}
	
	/**
	 * 发送消息
	 * @param message 消息对象
	 * @param properties 消息头属性
	 */
	public void sendUserMessage(User user, Map<String,Object> properties) {
		MessageHeaders messageHeaders = new MessageHeaders(properties);
		Message sendMessage = MessageBuilder.createMessage(user, messageHeaders);
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		// 消息唯一标识
		String id = UUID.randomUUID().toString();
		CorrelationData correlationData = new CorrelationData(id);
		rabbitTemplate.convertAndSend("com.gerry.springboot.exchange2", "springboot.123", user, correlationData);
	}

}

package com.gerry.pang.consumer;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.gerry.pang.entity.User;
import com.rabbitmq.client.Channel;



@Component
public class Receiver {

	@RabbitListener(
		bindings = @QueueBinding(
			exchange = @Exchange(name="com.gerry.springboot.exchange", durable="true", type="topic", autoDelete="false", internal="false", ignoreDeclarationExceptions="false"), 
			value = @Queue(name="com.gerry.springboot.queue", autoDelete="false", durable="true"),
			key = "springboot.*"
		)
	)
	@RabbitHandler
	public void receiveMessage(Message message, Channel channel) throws IOException {
		System.out.println("===== receive message =====");
		System.out.println("message:" + message);
		// 手工ack
		channel.basicAck((Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG), false);
	}
	
	/**
	 *  spring.rabbitmq.listener.order.exchange.name=com.gerry.springboot.exchange2
		spring.rabbitmq.listener.order.exchange.type=topic
		spring.rabbitmq.listener.order.exchange.durable=false
		spring.rabbitmq.listener.order.exchange.autoDelete=false
		spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions=false
		
		spring.rabbitmq.listener.order.queue.name=com.gerry.springboot.queue2
		spring.rabbitmq.listener.order.queue.autoDelete=false
		spring.rabbitmq.listener.order.queue.durable=false
		
		spring.rabbitmq.listener.order.key=springboot.*
		
	 * @param user
	 * @param header
	 * @param channel
	 * @throws IOException
	 */
	@RabbitListener(
		bindings = @QueueBinding(
			exchange = @Exchange(
					name="${spring.rabbitmq.listener.order.exchange.name}", 
					durable="${spring.rabbitmq.listener.order.exchange.durable}", 
					type="${spring.rabbitmq.listener.order.exchange.type}", 
					autoDelete="${spring.rabbitmq.listener.order.exchange.autoDelete}", 
					ignoreDeclarationExceptions="${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"), 
			value = @Queue(
					name="${spring.rabbitmq.listener.order.queue.name}", 
					autoDelete="${spring.rabbitmq.listener.order.queue.autoDelete}", 
					durable="${spring.rabbitmq.listener.order.queue.durable}"),
			key = "springboot.*"
		)
	)
	@RabbitHandler
	public void receiveMessage(@Payload User user, @Headers Map<String,Object> headers, Channel channel) throws IOException {
		// 注意：这里有两个坑
		// 1、入参的注解为headers不是header
		// 2、User必须实现Serializable接口，且和发送方为同一个包路径下的类，否则无法解析
		System.out.println("===== receive message user =====");
		System.out.println("message:" + user);
		// 手工ack
		channel.basicAck((Long)headers.get(AmqpHeaders.DELIVERY_TAG), false);
	}
	
}

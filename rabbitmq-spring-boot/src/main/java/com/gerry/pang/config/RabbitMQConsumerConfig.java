package com.gerry.pang.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Channel;

@Configuration
public class RabbitMQConsumerConfig {
	@Autowired
	@Qualifier("clientConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	private static int count = 1;
	
	@Autowired
	private final RabbitProperties properties;

	public RabbitMQConsumerConfig(RabbitProperties properties) {
		this.properties = properties;
	}


	@Bean
	public SimpleMessageListenerContainer messageListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames("MQ.clinet.topic.demo");
        // 设置并发消费者数量
		container.setConcurrentConsumers(properties.getListener().getSimple().getConcurrency());
		// 设置最大并发消费者数量
		container.setMaxConcurrentConsumers(properties.getListener().getSimple().getMaxConcurrency());
		// 设置每次获取的消息数量
		container.setPrefetchCount(properties.getListener().getSimple().getPrefetch());
		// 设置消息签收模式
		container.setAcknowledgeMode(properties.getListener().getSimple().getAcknowledgeMode());
		// 消息接收监听
		container.setMessageListener((ChannelAwareMessageListener) (message,channel) -> onMessage(message, channel));
		// 消费者名称标识
		container.setConsumerTagStrategy(queue -> createConsumerTag(queue));
		// 设置消费者的Arguments
		Map<String, Object> args = new HashMap<>();
		args.put("module","订单模块");
		args.put("fun","发送消息");
		// 消费者参数
		container.setConsumerArguments(args);
		return container;
	}
	
	private void onMessage(Message message, Channel channel) {
		System.out.println("====接收到消息=====");
		System.out.println(message.getMessageProperties());
		System.out.println(new String(message.getBody()));
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println("消息已经确认");
	}
	
	
	private String createConsumerTag(String queue) {
		return queue + (++ count);
	}

}

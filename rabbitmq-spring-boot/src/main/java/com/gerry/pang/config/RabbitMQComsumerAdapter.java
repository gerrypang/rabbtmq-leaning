package com.gerry.pang.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gerry.pang.service.MessageHandler;

@Configuration
public class RabbitMQComsumerAdapter {
	
	@Autowired
	@Qualifier("clientConnectionFactory")
	private ConnectionFactory connectionFactory;
	
	@Autowired
	private final RabbitProperties properties;
	
	public RabbitMQComsumerAdapter(RabbitProperties properties) {
		this.properties = properties;
	}
	
	@Bean
	public SimpleMessageListenerContainer messageListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		// 设置所有的监听队列
		container.setQueueNames("MQ.clinet.topic.demo","MQ.clinet.direct.demo");
		// 设置消息签收模式
		container.setAcknowledgeMode(properties.getListener().getSimple().getAcknowledgeMode());
        // 设置并发消费者数量
		container.setConcurrentConsumers(properties.getListener().getSimple().getConcurrency());
		// 设置最大并发消费者数量
		container.setMaxConcurrentConsumers(properties.getListener().getSimple().getMaxConcurrency());
		// 设置每次获取的消息数量
		container.setPrefetchCount(properties.getListener().getSimple().getPrefetch());
		
		// 消息适配器
		MessageListenerAdapter messageAdapter = new MessageListenerAdapter();
		// 设置默认接收消息处理方法
//		messageAdapter.setDefaultListenerMethod("handleMessage");
		messageAdapter.setDelegate(messageHandler());
		Map<String, String> queueToMethodMap = new HashMap<>();
		queueToMethodMap.put("MQ.clinet.direct.demo", "handleDirectMessage");
		queueToMethodMap.put("MQ.clinet.topic.demo", "handleTopicMessage");
		// 设置根据不同的队列不同处理方法
		messageAdapter.setQueueOrTagToMethodName(queueToMethodMap);
		// 设置消息接收转换方法
		messageAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
		// 设置消息监听
		container.setMessageListener(messageAdapter);
		
		return container;
	}
	
	@Bean
	public MessageHandler messageHandler() {
		return new MessageHandler();
	}
}

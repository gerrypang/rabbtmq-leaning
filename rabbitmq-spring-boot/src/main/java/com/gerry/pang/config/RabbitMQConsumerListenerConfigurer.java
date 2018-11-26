package com.gerry.pang.config;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerListenerConfigurer implements RabbitListenerConfigurer{

	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
//		SimpleRabbitListenerEndpoint endpoint1 = new SimpleRabbitListenerEndpoint();
//		endpoint1.setQueueNames("MQ.clinet.direct.demo");
//		endpoint1.setId("10111");
//		//endpoint1.setConcurrency("2");
//		endpoint1.setMessageListener( message -> {
//            System.out.println("endpoint1处理消息的逻辑");
//            System.out.println(new String(message.getBody()));
//        });
//		
//		SimpleRabbitListenerEndpoint endpoint2 = new SimpleRabbitListenerEndpoint();
//		endpoint2.setQueueNames("MQ.clinet.topic.demo");
//		endpoint2.setId("10222");
//		//endpoint2.setConcurrency("4");
//		endpoint2.setMessageListener(message -> {
//            System.out.println("endpoint2处理消息的逻辑");
//            System.out.println(new String(message.getBody()));
//        });
//		registrar.registerEndpoint(endpoint1);
//		registrar.registerEndpoint(endpoint2);
	}
	
}

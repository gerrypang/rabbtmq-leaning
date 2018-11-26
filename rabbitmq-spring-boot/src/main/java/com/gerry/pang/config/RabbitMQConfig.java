package com.gerry.pang.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitMQConfig {
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Autowired
	private final RabbitProperties properties;
	
	public RabbitMQConfig(RabbitProperties properties) {
		super();
		this.properties = properties;
	}

	/**
	 * 创建Connection bean<br>
	 * 注意：返回值建议用ConnectionFactory接口，以便于后期扩展其他MQ
	 * @return
	 */
	@Bean
	public ConnectionFactory clientConnectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(properties.getHost());
		connectionFactory.setPort(properties.getPort());
		connectionFactory.setUsername(properties.getUsername());
		connectionFactory.setPassword(properties.getPassword());
		connectionFactory.setVirtualHost(properties.getVirtualHost());
		// channel链接缓存设置（how many idle open connections are allowed）
		//connectionFactory.setChannelCacheSize(properties.getCache().getChannel().getSize());
		// 开启消息确认失败回调
		connectionFactory.setPublisherConfirms(properties.isPublisherConfirms());
		// 开启发送失败退回回调
		connectionFactory.setPublisherReturns(properties.isPublisherReturns());
		//connectionFactory.setExecutor(executor);
		// 设置connection的名字，设置为当前应用的名字
		connectionFactory.setConnectionNameStrategy(this::connectionName);
		return connectionFactory;
	}
	
	/**
	 * 创建Connection bean<br>
	 * 注意：返回值建议用ConnectionFactory接口，以便于后期扩展其他MQ
	 * @return
	 */
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(properties.getHost());
		connectionFactory.setPort(properties.getPort());
		connectionFactory.setUsername(properties.getUsername());
		connectionFactory.setPassword(properties.getPassword());
		connectionFactory.setVirtualHost(properties.getVirtualHost());
		// channel链接缓存设置（how many idle open connections are allowed）
		//connectionFactory.setChannelCacheSize(properties.getCache().getChannel().getSize());
		// 开启消息确认失败回调
		connectionFactory.setPublisherConfirms(properties.isPublisherConfirms());
		// 开启发送失败退回回调
		connectionFactory.setPublisherReturns(properties.isPublisherReturns());
		//connectionFactory.setExecutor(executor);
		// 设置connection的名字，设置为当前应用的名字
		connectionFactory.setConnectionNameStrategy(this::connectionName);
		return connectionFactory;
	}
	
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 设置忽略声明异常
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
		return rabbitAdmin;
	}
	
	@Bean
	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		// 重试策略模板
		RetryTemplate retryTemplate = new RetryTemplate();
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(500);
		backOffPolicy.setMultiplier(10.0);
		backOffPolicy.setMaxInterval(10000);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		rabbitTemplate.setRetryTemplate(retryTemplate);
		/**
		 * 为true 时，交换器无法根据类型和路由键找到符合的队列，调用Basic.Return 命令将消息返回给生产者。
		 * 为false 时，出现上述情形，则消息直接被丢弃。
		 */
		rabbitTemplate.setMandatory(properties.getTemplate().getMandatory());
		// 设置消息转换器
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		// 回调方法
		rabbitTemplate.setConfirmCallback(this::confirmCallback);
		rabbitTemplate.setReturnCallback(this::returnedMessage);
		return rabbitTemplate;
	}
	
	@Bean
	public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(@Qualifier("clientConnectionFactory") ConnectionFactory clientConnectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(clientConnectionFactory);
		factory.setConcurrentConsumers(3);
		factory.setMaxConcurrentConsumers(10);
		factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		factory.setPrefetchCount(1);
		factory.setMessageConverter(new Jackson2JsonMessageConverter());
		return factory;
	}
	
    @Bean
    public RabbitListenerConfigurer rabbitListenerConfigurer(){
    	return new RabbitMQConsumerListenerConfigurer();
    }
	
   private void confirmCallback(CorrelationData correlationData, boolean isAck, String cause) {
	   if (!isAck) {
			log.error("====== 发送消息失败，messageId:{}，失败原因：{} ====== ", 
					correlationData.getId(), cause);
		} else {
			log.info("====== 发送消息成功 ======");
		}
    }
   
   private void returnedMessage(Message message, int replyCode, String replyText,
			String exchange, String routingKey) {
	   log.info("returnedMessage message:{}, replyCode:{}, replyText:{}, exchange:{}, routingKey:{}", 
			   message, replyCode, replyText, exchange, routingKey);
   }
   
   @Bean
   public String connectionName(ConnectionFactory connectionFactory) {
	   return applicationName;
   }
	
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
}

package com.gerry.pang.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.gerry.pang.dto.Order;
import com.gerry.pang.dto.User;
import com.gerry.pang.mesage.MessageDelegate;

@Configuration
@ComponentScan({"com.gerry.pang.*"})
public class AMQPConfig {
	/**
	 * 底层可以从spring容器的@Bean声明获取Exchange，Queue，Bangding
	 * 然后使用RabbitTamplate excuate方法进行声明、删除、绑定exchange、queue，清空消息队列等操作
	 */

	/**
	 * 创建ConnectionFactory
	 * @return
	 */
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("127.0.0.1:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}
	
	/**
	 * 创建RabbitAdmin
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		// 不写也可以，默认为true
		// 注意：autostartup必须设置为true，否者spring容器启动时不会加载RabbitAdmin类
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}
	
	/**
	 * 创建RabbitTemplate
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
	}
	
	/**
	 * 声明topic类型的exchange
	 * @return
	 */
	@Bean
	public TopicExchange topicExchange() {
		Map<String, Object> arguments = new HashMap<>();
		return new TopicExchange("com.gerry.pang.topic.exchange", true, false, arguments);
	}
	
	/**
	 * 声明queue
	 * @return
	 */
	@Bean
	public Queue topicQueue() {
		Map<String, Object> arguments = new HashMap<>();
		return new Queue("com.gerry.pang.topic.queue", true, false, false, arguments);
	}
	
	/**
	 * 声明绑定
	 * @return
	 */
	@Bean
	public Binding binding001() {
		return BindingBuilder.bind(topicQueue()).to(topicExchange()).with("amqp.abc.*");
	}
	
	@Bean
	public DirectExchange directExchange() {
		Map<String, Object> arguments = new HashMap<>();
		return new DirectExchange("com.gerry.pang.direct.exchange", true, false, arguments);
	}
	
	@Bean
	public Queue directQueue() {
		Map<String, Object> arguments = new HashMap<>();
		return new Queue("com.gerry.pang.direct.queue", true, false, false, arguments);
	}
	
	@Bean
	public Binding binding002() {
		return BindingBuilder.bind(directQueue()).to(directExchange()).with("amqp.123");
	}
	
	@Bean
	public FanoutExchange fanoutExchange() {
		Map<String, Object> arguments = new HashMap<>();
		return new FanoutExchange("com.gerry.pang.fanout.exchange", true, false, arguments);
	}
	
	@Bean
	public Queue fanoutQueue() {
		Map<String, Object> arguments = new HashMap<>();
		return new Queue("com.gerry.pang.fanout.queue", true, false, false, arguments);
	}
	
	@Bean
	public Binding binding003() {
		return BindingBuilder.bind(fanoutQueue()).to(fanoutExchange());
	}
	
	@Bean
	public SimpleMessageListenerContainer messageListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(this.connectionFactory());
		// 设置监听的队列
		container.setQueues(topicQueue(), fanoutQueue(), directQueue());
		// 消息签收模式-自动
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		// 设置并发监听消费者数量1，最大并发前提数量5
		container.setConcurrentConsumers(1);
		container.setMaxConcurrentConsumers(5);
		// 消息拒绝重回队列-false
		container.setDefaultRequeueRejected(false);
		// 设置消息监听在channel层
		container.setExposeListenerChannel(true);
		// 设置消费者标签
		container.setConsumerTagStrategy(new ConsumerTagStrategy() {
			@Override
			public String createConsumerTag(String queue) {
				return queue + "_" + UUID.randomUUID();
			}
		});
		
		// 消息监听器方式接收消息
/*		container.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				System.out.println("---------接收消息-------");
				System.out.println(new String(message.getBody()));
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
		});*/
		
		/**
		 * （一）消息适配器方式接收消息
		 * 
		 * 1、适配器默认方法hanleMessage
		 * 2、修改默认方法setDefaultListenerMethod
		 * 3、修改消息转换器setMessageConverter, 默认转换器为SimpleMessageConverter,如果contextType为text/palin类型,方法接收入参可以stringer不是byte[]

		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
		messageListenerAdapter.setDefaultListenerMethod("consumeMessage");
		MessageConverter messageConverter = new SimpleMessageConverter();
		messageListenerAdapter.setMessageConverter(messageConverter);
		container.setMessageListener(messageListenerAdapter);
		*/
		
		/**
		 * （二）消息适配器方式接收消息
		 *  根据不同队列名称和方法名进行绑定,如果没有绑定的队列走hanleMessage

		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
		Map<String,String> queueMethodName = new HashMap<>();
		queueMethodName.put("com.gerry.pang.topic.queue", "method1");
		queueMethodName.put("com.gerry.pang.direct.queue", "method2");
		messageListenerAdapter.setQueueOrTagToMethodName(queueMethodName);
		container.setMessageListener(messageListenerAdapter);
		*/
		
		/**
		 * （三）转换器 Jackson2JsonMessageConverter
		 
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
		messageListenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
		messageListenerAdapter.setDefaultListenerMethod("jsonMethod");
		container.setMessageListener(messageListenerAdapter);
		*/	
		
		/**
		 * （四）转换器 DefaultJackson2JavaTypeMapper
		 *  单个对象
		
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
		Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
		DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
		// 如果不是java.util/java.lang包下面的类必须设置trustPackages,
		// 否则报异常If you believe this class is safe to deserialize, please provide its name. If the serialization is only done by a trusted source, you can also enable trust all (*).
		javaTypeMapper.setTrustedPackages("*");
		jsonConverter.setJavaTypeMapper(javaTypeMapper);
		messageListenerAdapter.setMessageConverter(jsonConverter);
		messageListenerAdapter.setDefaultListenerMethod("javaObjectMethod");
		container.setMessageListener(messageListenerAdapter);
		*/
		
		/**
		 * （五）转换器 DefaultJackson2JavaTypeMapper 多个对象
	
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
		Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
		DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
		Map<String, Class<?>> idClassMapping = new HashMap<>();
		idClassMapping.put("order", Order.class);
		idClassMapping.put("user", User.class);
		javaTypeMapper.setIdClassMapping(idClassMapping);
		// 如果不是java.util/java.lang包下面的类必须设置trustPackages
		javaTypeMapper.setTrustedPackages("com.gerry.pang.dto");
		jsonConverter.setJavaTypeMapper(javaTypeMapper);
		messageListenerAdapter.setMessageConverter(jsonConverter);
		messageListenerAdapter.setDefaultListenerMethod("javaObjectMethod");
		container.setMessageListener(messageListenerAdapter);
		*/
		
		/**
		 * （六）全局转换器 DefaultJackson2JavaTypeMapper
		 *  多个对象
		 */
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
		ContentTypeDelegatingMessageConverter contentTypeConverter = new ContentTypeDelegatingMessageConverter();
		Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
		Map<String, Class<?>> idClassMapping = new HashMap<>();
		DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
		idClassMapping.put("order", Order.class);
		idClassMapping.put("user", User.class);
		javaTypeMapper.setIdClassMapping(idClassMapping);
		// 如果不是java.util/java.lang包下面的类必须设置trustPackages
		javaTypeMapper.setTrustedPackages("com.gerry.pang.dto");
		jsonConverter.setJavaTypeMapper(javaTypeMapper);
		// json
		contentTypeConverter.addDelegate("json", jsonConverter);
		contentTypeConverter.addDelegate("application/json", jsonConverter);
		// text
		SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
		contentTypeConverter.addDelegate("text", simpleMessageConverter);
		contentTypeConverter.addDelegate("text/plain", simpleMessageConverter);
		// 总的convert
		messageListenerAdapter.setMessageConverter(contentTypeConverter);
		messageListenerAdapter.setDefaultListenerMethod("consumeMessage");
		container.setMessageListener(messageListenerAdapter);
		return container;
	}
	
}

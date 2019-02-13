package com.gerry.pang;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerry.pang.dto.Order;
import com.gerry.pang.dto.User;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

	@Autowired
	private RabbitAdmin rabbitAdmin; 
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Test
	public void testRabbitAdmin() throws JsonProcessingException {
		// 方法一：声明exchange、队列和绑定
		rabbitAdmin.declareExchange(new DirectExchange("spring.amqp.directexchange", true, false, null));
		rabbitAdmin.declareQueue(new Queue("spring.amqp.direct.queue", true, false, false, null));
		rabbitAdmin.declareBinding(new Binding("spring.amqp.direct.queue", DestinationType.QUEUE, "spring.amqp.directexchange", "amqp.123", null));
		
		// 方法二：声明exchange、队列，通过BingdingBuilder方式绑定队列
		rabbitAdmin.declareExchange(new TopicExchange("spring.amqp.topicexchange", true, false, null));
		rabbitAdmin.declareQueue(new Queue("spring.amqp.topic.queue", true, false, false, null));
		Binding bangding = BindingBuilder
			.bind(new Queue("spring.amqp.topic.queue", true, false, false, null))
			.to(new TopicExchange("spring.amqp.topicexchange", true, false, null))
			.with("amqp.*");
		rabbitAdmin.declareBinding(bangding);
	}
	
	/**
	 * （一）消息适配器方式接收消息
	 * （二）消息适配器方式接收消息 根据不同队列名称和方法名进行绑定,如果没有绑定的队列走hanleMessage
	 */
	@Test
	public void testSendMessage() {
		// 创建MessageProperties消息属性对象，配置属性
		MessageProperties properties = new MessageProperties();
		properties.getHeaders().put("hello", "gerry");
		properties.setContentEncoding("UTF-8");
		properties.setContentType("text/plain");
		// 创建消息，配置属性
		Message message = new Message("第一条消息".getBytes(), properties);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange","amqp.abc.123", message);
		
		
		// 方法二：发送消息，并创建内部类MessagePostProcessor，在发送前修改部分属性
		rabbitTemplate.convertAndSend("com.gerry.pang.topic.exchange","amqp.abc.13", message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				message.getMessageProperties().setHeader("attr", "123");
				return message;
			}
		});
		
		// 方法三：发送消息
		rabbitTemplate.convertAndSend("com.gerry.pang.direct.exchange","amqp.123", "第二条消息");
	}
	
	/**
	 * （三）转换器 Jackson2JsonMessageConverter
	 * @throws JsonProcessingException
	 */
	@Test
	public void testSendJsonMessage() throws JsonProcessingException {
		// 创建MessageProperties消息属性对象，配置属性
		MessageProperties properties = new MessageProperties();
		properties.getHeaders().put("hello", "gerry");
		properties.setContentEncoding("UTF-8");
		// 发送json格式的消息
		properties.setContentType("application/json");
		User user = new User();
		user.setAge(11);
		user.setClassName("12300");
		user.setName("21212");
		// 创建消息，配置属性
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);
		System.out.println("=======>"+ json);
		Message message = new Message(json.getBytes(), properties);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange","amqp.abc.123", message);
	}
	
	/**
	 * （四）转换器 DefaultJackson2JavaTypeMapper 单个对象
	 * @throws JsonProcessingException
	 */
	@Test
	public void testSendJavaObjectMessage() throws JsonProcessingException {
		// 创建MessageProperties消息属性对象，配置属性
		MessageProperties properties = new MessageProperties();
		properties.getHeaders().put("hello", "gerry");
		properties.setContentEncoding("UTF-8");
		// 必须加上这个__TypeId__, DefaultJackson2JavaTypeMapper才能正常转换
		properties.getHeaders().put("__TypeId__", "com.gerry.pang.dto.User");
		// 这里注意一定要修改contentType为 application/json
		properties.setContentType("application/json");
		// 发送json格式的消息
		User user = new User();
		user.setAge(11);
		user.setClassName("12300");
		user.setName("21212");
		// 创建消息，配置属性
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);
		System.out.println("=======>"+ json);
		Message message = new Message(json.getBytes(), properties);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange", "amqp.abc.123", message);
	}
	
	/**
	 * （五）转换器 DefaultJackson2JavaTypeMapper 多个对象
	 * @throws JsonProcessingException
	 */
	@Test
	public void testSendMultiJavaObjectMessage() throws JsonProcessingException {
		// 创建MessageProperties消息属性对象，配置属性
		MessageProperties properties1 = new MessageProperties();
		properties1.getHeaders().put("hello", "gerry");
		properties1.setContentEncoding("UTF-8");
		// 必须加上这个__TypeId__, DefaultJackson2JavaTypeMapper才能正常转换
		properties1.getHeaders().put("__TypeId__", "user");
		// 这里注意一定要修改contentType为 application/json
		properties1.setContentType("application/json");
		// 发送json格式的消息
		User user = new User();
		user.setAge(11);
		user.setClassName("12300");
		user.setName("21212");
		// 创建消息，配置属性
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);
		System.out.println("=======>"+ json);
		Message message = new Message(json.getBytes(), properties1);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange", "amqp.abc.123", message);
		
		MessageProperties properties2 = new MessageProperties();
		properties2.getHeaders().put("hello", "gerry");
		properties2.setContentEncoding("UTF-8");
		// 必须加上这个__TypeId__, DefaultJackson2JavaTypeMapper才能正常转换
		properties2.getHeaders().put("__TypeId__", "order");
		// 这里注意一定要修改contentType为 application/json
		properties2.setContentType("application/json");
		Order order = new Order();
		order.setId("12432423");
		order.setContent("hello world");
		order.setName("jjjjlsjlfkdd");
		System.out.println("=======>"+ json);
		json = mapper.writeValueAsString(order);
		message = new Message(json.getBytes(), properties2);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange", "amqp.abc.123", message);
	}
	
	/**
	 * （六）全局转换器 DefaultJackson2JavaTypeMapper
	 * @throws JsonProcessingException
	 */
	@Test
	public void testSendComplexMessage() throws JsonProcessingException {
		// 创建MessageProperties消息属性对象，配置属性
		MessageProperties properties1 = new MessageProperties();
		properties1.getHeaders().put("hello", "gerry");
		properties1.setContentEncoding("UTF-8");
		// 必须加上这个__TypeId__, DefaultJackson2JavaTypeMapper才能正常转换
		properties1.getHeaders().put("__TypeId__", "user");
		// 这里注意一定要修改contentType为 application/json
		properties1.setContentType("application/json");
		// 发送json格式的消息
		User user = new User();
		user.setAge(11);
		user.setClassName("12300");
		user.setName("21212");
		// 创建消息，配置属性
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);
		System.out.println("=======>"+ json);
		Message message = new Message(json.getBytes(), properties1);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange", "amqp.abc.123", message);
		
		MessageProperties properties2 = new MessageProperties();
		properties2.getHeaders().put("hello", "gerry");
		properties2.setContentEncoding("UTF-8");
		// 必须加上这个__TypeId__, DefaultJackson2JavaTypeMapper才能正常转换
		properties2.getHeaders().put("__TypeId__", "order");
		// 这里注意一定要修改contentType为 application/json
		properties2.setContentType("application/json");
		Order order = new Order();
		order.setId("12432423");
		order.setContent("hello world");
		order.setName("jjjjlsjlfkdd");
		System.out.println("=======>"+ json);
		json = mapper.writeValueAsString(order);
		message = new Message(json.getBytes(), properties2);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange", "amqp.abc.123", message);
		
		// 创建MessageProperties消息属性对象，配置属性
		MessageProperties properties3 = new MessageProperties();
		properties3.getHeaders().put("hello", "gerry");
		properties3.setContentEncoding("UTF-8");
		properties3.setContentType("text/plain");
		// 创建消息，配置属性
		message = new Message("第一条消息".getBytes(), properties3);
		// 方法一：发送消息
		rabbitTemplate.send("com.gerry.pang.topic.exchange", "amqp.abc.123", message);
	}
}

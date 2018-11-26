package com.gerry.pang.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitAdmin会根据application bean自动在rabbitMQ中创建Exchange、Queue和绑定关系
 * 
 * @author pangguowei
 * @date 2018年11月23日 上午10:56:26
 */
@Configuration
public class RabbitMQDeclearConfig {

	@Bean
	public Exchange dirctExchage() {
		return new DirectExchange("Ex.direct.spring.boot.demo", true, false);
	}
	
	@Bean
	public TopicExchange topicExchage() {
		return new TopicExchange("Ex.topic.spring.boot.demo", true, false);
	}
	
	/**
	 * 批量创建
	 * @return
	 */
	@Bean
	public List<Exchange> exchages() {
		List<Exchange> exchangeList = new ArrayList<>();
		exchangeList.add(new DirectExchange("Ex.direct.spring.boot.demo.1", true, false));
		exchangeList.add(new DirectExchange("Ex.direct.spring.boot.demo.2", true, false));
		exchangeList.add(new TopicExchange("Ex.topic.spring.boot.demo.3", true, false));
		return exchangeList;
	}
	
	@Bean
	public Queue infoQueue() {
		return new Queue("MQ.spring.boot.info", true);
	}
	
	@Bean
	public Queue debugQueue() {
		return new Queue("MQ.spring.boot.debug", true);
	}
	
	/**
	 * 批量创建
	 * @return
	 */
	@Bean
	public List<Queue> Queues() {
		List<Queue> queueList = new ArrayList<>();
		queueList.add(new Queue("MQ.spring.boot.debug.1", true));
		queueList.add(new Queue("MQ.spring.boot.debug.2", true));
		queueList.add(new Queue("MQ.spring.boot.debug.3", true));
		return queueList;
	}
	
	@Bean
	public Binding infoBingding() {
		Binding b = new Binding("MQ.spring.boot.info", Binding.DestinationType.QUEUE, 
				"Ex.direct.spring.boot.demo", "spring.boot.111", new HashMap<String, Object>());
		return b; 
	}
	
	@Bean
	public Binding debugBingding() {
		Binding b = BindingBuilder.bind(debugQueue()).to(topicExchage()).with("spring.boot.#");
		return b; 
	}
	
	//如果是new新的Queue和Exchange会不会成功
//	@Bean
//	public Binding debugBingding() {
//		Binding b = BindingBuilder.bind(new Queue("aaa")).to(new TopicExchange("bbb")).with("spring.boot.#");
//		return b; 
//	}
	
	/**
	 * 批量创建 
	 * @return
	 */
	@Bean
	public List<Binding> bingdings() {
		List<Binding> bindingList = new ArrayList<>();
		bindingList.add(new Binding("MQ.spring.boot.debug.1", Binding.DestinationType.QUEUE, "Ex.direct.spring.boot.demo.1", "spring.boot.1", new HashMap<String, Object>()));
		bindingList.add(new Binding("MQ.spring.boot.debug.2", Binding.DestinationType.QUEUE, "Ex.direct.spring.boot.demo.2", "spring.boot.2", new HashMap<String, Object>()));
		bindingList.add(new Binding("MQ.spring.boot.debug.3", Binding.DestinationType.QUEUE, "Ex.topic.spring.boot.demo.3",  "spring.boot.#", new HashMap<String, Object>()));
		return bindingList;
	}
}

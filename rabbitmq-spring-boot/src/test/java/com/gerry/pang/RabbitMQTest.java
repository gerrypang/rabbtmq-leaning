package com.gerry.pang;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitMQTest {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Test
	public void testSend() {
		 MessageProperties messageProperties = new MessageProperties(); 
		 messageProperties.getHeaders().put("desc","消息发送"); 
		 messageProperties.getHeaders().put("type",10); 
		 messageProperties.setAppId("aaa.bbb.ccc");
		 messageProperties.setConsumerTag("aaa.bbb.ccc.111.33");
		 messageProperties.setMessageId(System.currentTimeMillis()+"");
		 messageProperties.setContentEncoding("UTF-8");
		 messageProperties.setContentType("application/json");
		 messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		 messageProperties.setPriority(1);
		 Message message = new Message(simpleMessage().getBytes(), messageProperties);
		
		CorrelationData correlationData = new CorrelationData();
		correlationData.setId("112233");
		rabbitTemplate.send("EX.client.direct.demo", "com.123", message);		
		rabbitTemplate.send("EX.client.direct.demo", "com.123", message);		
		
		// 两个发送的消息是一样的，因为在convert中会判断object的类型是否为message
		rabbitTemplate.convertAndSend("EX.client.topic.demo", "com.abc.112", message, correlationData);
		rabbitTemplate.convertAndSend("EX.client.topic.demo", "com.abc.112", message, correlationData);
		
		rabbitTemplate.convertAndSend(simpleMessage(), this::postProcessMessage);
		try {
			TimeUnit.SECONDS.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public String simpleMessage() {
		String msg = "{" + 
			"    \"data\": {" + 
			"        \"id\": \"8a808553604ab67501605489a9d308dd\"," + 
			"        \"version\": null," + 
			"        \"createTime\": null," + 
			"        \"creatorId\": null," + 
			"        \"creatorName\": null," + 
			"        \"creatorDepartmentId\": null," + 
			"        \"creatorDepartmentName\": null," + 
			"        \"updateTime\": null," + 
			"        \"updatorId\": null," + 
			"        \"updatorName\": null," + 
			"        \"updatorDepartmentId\": null," + 
			"        \"updatorDepartmentName\": null," + 
			"        \"current\": 1," + 
			"        \"rowCount\": 10," + 
			"        \"deleted\": false," + 
			"        \"bzId\": null," + 
			"        \"currentUser\": null" + 
			"    }," + 
			"    \"errorMessage\": null," + 
			"    \"hasErrors\": false," + 
			"    \"success\": true" + 
			"}";
		return msg;
	}
	
	/**
	 * 发送消息的后置处理器<br>
	 * 对消息属性（参数、消息头）进行设置
	 * 
	 * @param message
	 * @return
	 * @throws AmqpException
	 */
	private Message postProcessMessage(Message message) throws AmqpException {
		System.out.println("-------处理前message-------------");
		System.out.println(message);
		MessageProperties messageProperties = message.getMessageProperties();
		messageProperties.getHeaders().put("desc", "消息发送");
		messageProperties.getHeaders().put("type", 10);
		messageProperties.setAppId("aaa.bbb.ccc");
		messageProperties.setConsumerTag("aaa.bbb.ccc.111.33");
		messageProperties.setMessageId(System.currentTimeMillis() + "");
		messageProperties.setContentEncoding("UTF-8");
		messageProperties.setContentType("application/json");
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		messageProperties.setPriority(1);
		return message;
	}

}

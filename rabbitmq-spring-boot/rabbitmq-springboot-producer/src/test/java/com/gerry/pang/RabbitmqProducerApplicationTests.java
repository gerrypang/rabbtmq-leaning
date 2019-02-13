package com.gerry.pang;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerry.pang.entity.User;
import com.gerry.pang.producer.Sender;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqProducerApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Autowired
	private Sender sender;
	
	private static SimpleDateFormat simpleDateFromat = new SimpleDateFormat("yyyy-MM-dd HH;mm:ss.SSS");

	@Test
	public void testSendMessage() {
		String message = "springBoot-rabbitmq hello world";
		Map<String, Object> properties = new HashMap<>();
		properties.put("send_time", simpleDateFromat.format(new Date()));
		sender.sendMessage(message, properties);
	}
	
	@Test
	public void testSendUserMessage() {
		Map<String, Object> properties = new HashMap<>();
		properties.put("send_time", simpleDateFromat.format(new Date()));
		User user = new User();
		user.setId("2222222222");
		user.setAge(12);
		user.setName("张三");
		sender.sendUserMessage(user, properties);
	}
}


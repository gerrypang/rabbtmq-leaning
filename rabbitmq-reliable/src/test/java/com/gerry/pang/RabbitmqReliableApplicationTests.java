package com.gerry.pang;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerry.pang.entity.Order;
import com.gerry.pang.service.OrderService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqReliableApplicationTests {

	@Autowired
	private OrderService orderService;
	
	@Test
	public void sendTest() {
		Order order = new Order();
		order.setId(System.currentTimeMillis()+"");
		order.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID());
		order.setName("test send rabbitmq");
		orderService.createOrder(order);
	}

}

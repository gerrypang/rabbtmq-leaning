package com.gerry.rabbit.client.quickstart;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 消费者
 * @author Gerry_Pang
 */
public class Comsumer {

	public static void main(String[] args) {
		// 1 创建连接工厂，并进行配置
		ConnectionFactory connnectionFactory = new ConnectionFactory();
		connnectionFactory.setHost("127.0.0.1");
		connnectionFactory.setPort(5672);
		connnectionFactory.setVirtualHost("/");
		String queueName = "test001";
		try {
			// 2 根据连接工厂创建连接
			Connection connection = connnectionFactory.newConnection();
			// 3 通过connection创建channel
			Channel channel = connection.createChannel();
			// 4 声明（创建）队列
			channel.queueDeclare(queueName, true, false, false, null);
			// 5 创建消费者
			QueueingConsumer consumer = new QueueingConsumer(channel); 
			// 6 设置channel
			channel.basicConsume(queueName, true, consumer);
			while(true) {
				// 7 接收消息
				Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println("消费端: " + message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

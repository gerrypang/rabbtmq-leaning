package com.gerry.pang.rabbitmq.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.gerry.pang.rabbitmq.prepare.PerpareExchange;
import com.gerry.pang.rabbitmq.prepare.PrepareQueue;
import com.gerry.pang.rabbitmq.woker.SimpleConsumer;
import com.gerry.pang.rabbitmq.woker.SimpleProducter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * java client 方式发送接收消息<br>
 *  参考资料:<br>
 *  https://www.jianshu.com/p/05ec99363ba1<br>
 *  http://www.rabbitmq.com/api-guide.html<br>
 * 
 * @author pangguowei
 * @date 2018年11月21日 上午10:12:22
 */
public class RabbitMQClientDemo {
	
	/**
	 * 创建Connection连接
	 * @return
	 */
	public Connection createConnection() {
		ConnectionFactory connectFactory = new ConnectionFactory();
		Connection connection = null;
		connectFactory.setUsername("guest");
		connectFactory.setPassword("guest");
		connectFactory.setPort(5672);
		connectFactory.setHost("127.0.0.1");
		connectFactory.setVirtualHost("/");
		try {
			connection = connectFactory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * 创建 channel
	 * @param conn
	 * @return
	 */
	public Channel createChannel(Connection conn) {
		Channel channel = null; 
		try {
			channel = conn.createChannel(3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return channel;
	}
	
	/**
	 * 关闭所有channel和Connection
	 * @param conn
	 * @param channel
	 */
	public void closeLink(Connection conn, Channel channel) {
		try {
			if (channel != null) {
				channel.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 准备exchang和queue，绑定之间的关系
	 * 
	 * @param channel
	 */
	public void prepare(Channel channel) {
		try {
			PrepareQueue prepareQueue = new PrepareQueue();
			prepareQueue.declearQueue(channel);
			PerpareExchange prepareExchange = new PerpareExchange();
			prepareExchange.declearExchanege(channel);
			channel.queueBind("MQ.clinet.topic.demo", "EX.client.topic.demo", "com.abc.*");
			channel.queueBind("MQ.clinet.direct.demo", "EX.client.direct.demo", "com.123");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 接收消息
	 * @param channel
	 */
	public static void receiveMessage(Channel channel) {
		System.out.println("===== 开始接收消息 =====");
		boolean autoAck = false; 
		SimpleConsumer consumerA = new SimpleConsumer(channel);
		SimpleConsumer consumerB = new SimpleConsumer(channel);
		try {
			// 注意：consumer tag 不能相同
			channel.basicConsume("MQ.clinet.topic.demo", autoAck, "gerry pang topic", consumerA);
			channel.basicConsume("MQ.clinet.direct.demo", autoAck, "gerry pang direct", consumerB);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("===== 完成接收消息 =====");
	}
	
	/**
	 * 发送消息
	 * @param channel
	 */
	public static void sendMessage(Channel channel) {
		System.out.println("===== 开始发送消息 =====");
		SimpleProducter producter = new SimpleProducter();
		producter.sendMessage(channel);
		System.out.println("===== 完成发送消息 =====");
	}
	
	public static void main(String[] args) {
		try {
			RabbitMQClientDemo demo = new RabbitMQClientDemo();
			Connection conn = demo.createConnection();
			Channel channel = demo.createChannel(conn);
			//demo.prepare(channel);
			sendMessage(channel);
			receiveMessage(channel);
			
			TimeUnit.SECONDS.sleep(60);
			demo.closeLink(conn, channel);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

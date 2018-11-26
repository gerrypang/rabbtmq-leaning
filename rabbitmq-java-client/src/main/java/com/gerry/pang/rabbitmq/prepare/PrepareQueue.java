package com.gerry.pang.rabbitmq.prepare;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;

public class PrepareQueue {
	
	public void declearQueue(Channel channel) {
		try {
		    // 设置属性
	        Map<String, Object> arguments = new HashMap<>();
	        arguments.put("hello", "world");
	        arguments.put("queue-argumet", "demo啊");
	        
			/**
			 * 同步声明queue操作，有返回值
			 * queue: the name of the queue
		     * durable: true if we are declaring a durable queue (the queue will survive a server restart)
		     * exclusive: true if we are declaring an exclusive queue (restricted to this connection)
		     * autoDelete: true if we are declaring an autodelete queue (server will delete it when no longer in use)
		     * arguments: other properties (construction arguments) for the queue
			 */
	        Queue.DeclareOk topicQueueOk = channel.queueDeclare("MQ.clinet.topic.demo", true, false, false, arguments);
	        System.out.println("===== topic type queue declear " + topicQueueOk + " ======");
	        Queue.DeclareOk directQueueOk = channel.queueDeclare("MQ.clinet.direct.demo", true, false, false, arguments);
	        System.out.println("===== direct type queue declear " + directQueueOk + " ======");
			
	        /**
			 * 异步声明queue操作，无返回值
			 * queue: the name of the queue
		     * durable: true if we are declaring a durable queue (the queue will survive a server restart)
		     * exclusive: true if we are declaring an exclusive queue (restricted to this connection)
		     * autoDelete: true if we are declaring an autodelete queue (server will delete it when no longer in use)
		     * arguments: other properties (construction arguments) for the queue
			 */
	        channel.queueDeclareNoWait("MQ.clinet.nowait.demo", true, false, false, arguments);
	        
	        // 检查queue是否
	        Queue.DeclareOk exists = channel.queueDeclarePassive("MQ.clinet.nowait.demo");
	        System.out.println("===== " + exists + "======");
	        
			// 删除已经声明的queue
			Queue.DeleteOk deleted = channel.queueDelete("MQ.clinet.nowait.demo");
			System.out.println("===== " + deleted + "======");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

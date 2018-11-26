package com.gerry.pang.rabbitmq.prepare;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.Exchange;

public class PerpareExchange {
	
	public void declearExchanege(Channel channel) {
		try {
		    // 设置属性
	        Map<String, Object> arguments = new HashMap<>();
	        arguments.put("hello", "world");
	        arguments.put("exchange-argumet", "demo啊");
	       
	        /**
	         *  同步声明exchange操作，有返回值
	         * exchange: the name of the exchange
	         * type: the exchange type
	         * durable: true if we are declaring a durable exchange (the exchange will survive a server restart)
	         * autoDelete: true if the server should delete the exchange when it is no longer in use
	         * arguments: other properties (construction arguments) for the exchange
	         */
			AMQP.Exchange.DeclareOk topicExchangeOk = channel.exchangeDeclare("EX.client.topic.demo", BuiltinExchangeType.TOPIC, true, false, arguments);
			System.out.println("===== topic type exchange declear " + topicExchangeOk + " ======");
			AMQP.Exchange.DeclareOk directExchangeOk = channel.exchangeDeclare("EX.client.direct.demo", BuiltinExchangeType.DIRECT, true, false, arguments);
			System.out.println("===== direct type exchange declear " + directExchangeOk + " ======");
			
			/**
			 *  异步声明exchange操作，无返回值
			 * exchange: the name of the exchange
		     * type: the exchange type
		     * durable: true if we are declaring a durable exchange (the exchange will survive a server restart)
		     * autoDelete: true if the server should delete the exchange when it is no longer in use
		     * internal: true if the exchange is internal, i.e. can't be directly published to by a client.
		     * arguments: other properties (construction arguments) for the exchange
			 */
			channel.exchangeDeclareNoWait("EX.client.nowait.demo", BuiltinExchangeType.DIRECT, true, false, false, arguments);
			
			// 检查exchange是否
			Exchange.DeclareOk exists = channel.exchangeDeclarePassive("EX.client.nowait.demo");
			System.out.println("===== " + exists + "======");
			
			// 删除已经声明的exchangge
			Exchange.DeleteOk deleted = channel.exchangeDelete("EX.client.nowait.demo");
			System.out.println("===== " + deleted + "======");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

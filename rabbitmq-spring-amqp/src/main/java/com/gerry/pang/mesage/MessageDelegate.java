package com.gerry.pang.mesage;

import java.util.Map;

import com.gerry.pang.dto.Order;
import com.gerry.pang.dto.User;

public class MessageDelegate {
	
	/**
	 * 默认名称 handleMessage
	 * @param messageBody
	 */
	public void handleMessage(String messageBody) {
		System.out.println("------ MessageListenerAdapter handleMessage ------");
		System.out.println(messageBody);
	}
	
	/**
	 * 默认名称 handleMessage
	 * @param messageBody
	 */
	public void handleMessage(byte[] messageBody) {
		System.out.println("------ MessageListenerAdapter handleMessage ------");
		System.out.println(new String(messageBody));
	}
	
	/**
	 * 自定义名称 consumeMessage
	 * @param messageBody
	 */
	public void consumeMessage(String messageBody) {
		System.out.println("------ MessageListenerAdapter consumeMessage string ------");
		System.out.println(messageBody);
	}
	
	public void consumeMessage(Order order) {
		System.out.println("------ MessageListenerAdapter consumeMessage order ------");
		System.out.println(order);
	}
	
	public void consumeMessage(User user) {
		System.out.println("------ MessageListenerAdapter consumeMessage user ------");
		System.out.println(user);
	}
	
	/**
	 * 不同消息队列绑定不同的接收方法
	 * @param message
	 */
	public void method1(String message) {
		System.out.println("------ MessageListenerAdapter method1 ------");
		System.out.println(message);
	}
	public void method2(String message) {
		System.out.println("------ MessageListenerAdapter method2 ------");
		System.out.println(message);
	}
	
	/**
	 * 接收json格式消息
	 * @param message
	 */
	public void jsonMethod(Map message) {
		System.out.println("------ MessageListenerAdapter jsonMethod ------");
		System.out.println(message);
	}
	
	public void javaObjectMethod(User user) {
		System.out.println("------ MessageListenerAdapter javaObjectMethod ------");
		System.out.println(user);
	}
}

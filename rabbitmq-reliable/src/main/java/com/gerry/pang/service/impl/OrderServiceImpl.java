package com.gerry.pang.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gerry.pang.common.constant.SystemConsts.MessageConstants;
import com.gerry.pang.entity.BrokerMessageLog;
import com.gerry.pang.entity.Order;
import com.gerry.pang.mapper.BrokerMessageLogMapper;
import com.gerry.pang.mapper.OrderMapper;
import com.gerry.pang.service.OrderService;
import com.gerry.pang.utils.rabbitmq.RabbitMQSender;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private BrokerMessageLogMapper brokerMessageLogMapper;
	
	@Autowired
	private RabbitMQSender sender;

	@Override
	public void createOrder(Order order) {
		Date orderTime = new Date();
		// order信息插入数据到MySQL
		orderMapper.insert(order);
		BrokerMessageLog brokerMessageLog = new BrokerMessageLog();
		brokerMessageLog.setMessageId(order.getMessageId());
		brokerMessageLog.setMessage(JSONObject.toJSONString(order));
		brokerMessageLog.setStatus(MessageConstants.ORDER_SENDING);
		brokerMessageLog.setUpdateTime(orderTime);
		brokerMessageLog.setCreateTime(orderTime);
		// order log 插入数据到MySQL
		brokerMessageLogMapper.insert(brokerMessageLog);
		// 发送消息
		sender.sendMessage(order);
	}

}

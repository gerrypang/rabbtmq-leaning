package com.gerry.pang.utils.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.gerry.pang.common.constant.SystemConsts.MessageConstants;
import com.gerry.pang.entity.BrokerMessageLog;
import com.gerry.pang.entity.Order;
import com.gerry.pang.mapper.BrokerMessageLogMapper;
import com.gerry.pang.utils.rabbitmq.RabbitMQSender;

@Component
public class RetryMessageTasker {

	@Autowired
	private RabbitMQSender sender;
	
	@Autowired
	private BrokerMessageLogMapper brokerMessageLogMapper;
	
	@Scheduled(initialDelay = 5000, fixedDelay = 2000)
	public void reSend() {
		List<BrokerMessageLog> timeOutlist = brokerMessageLogMapper.query4StatusAndTimeoutMessage();
		timeOutlist.forEach( messageLog -> {
			if (messageLog.getTryCount() > 3) {
				brokerMessageLogMapper.changeBrokerMessageLogStatus(messageLog.getMessageId(), MessageConstants.ORDER_SEND_FAILURE, new Date());
			} else {
				brokerMessageLogMapper.changeBrokerMessageLogStatus(messageLog.getMessageId(), MessageConstants.ORDER_SENDING, new Date());
				Order order = JSONObject.parseObject(messageLog.getMessage(), Order.class);
				sender.sendMessage(order);
			}
		});
	}
}

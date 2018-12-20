package com.gerry.pang.utils.rabbitmq;

import java.util.Date;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gerry.pang.common.constant.SystemConsts.MessageConstants;
import com.gerry.pang.entity.Order;
import com.gerry.pang.mapper.BrokerMessageLogMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RabbitMQSender {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private BrokerMessageLogMapper brokerMessageLogMapper;
	
	//回调函数: confirm确认
    final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            log.info("correlationData: {}", correlationData);
            String messageId = correlationData.getId();
			if (ack) {
				// 如果confirm返回成功 则进行更新
				brokerMessageLogMapper.changeBrokerMessageLogStatus(messageId, 
						MessageConstants.ORDER_SEND_SUCCESS,
						new Date());
				log.info("==== 发送成功 ====");
			} else {
				// 失败则进行具体的后续操作:重试 或者补偿等手段
				log.error("异常处理...");
			}
        }
    };

	public void sendMessage(Message message) {
		rabbitTemplate.setConfirmCallback(confirmCallback);
        //消息唯一ID
        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getMessageId());
        rabbitTemplate.convertAndSend("order-exchange", "order.ABC", message, correlationData);
	}
	
	public void sendMessage(Order order) {
		rabbitTemplate.setConfirmCallback(confirmCallback);
		//消息唯一ID
		CorrelationData correlationData = new CorrelationData(order.getMessageId());
		rabbitTemplate.convertAndSend("order-exchange", "order.ABC", order, correlationData);
	}
}

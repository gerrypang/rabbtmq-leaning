package com.gerry.pang.service;

import com.gerry.pang.entity.Order;

public interface OrderService {

	/**
	 * 创建订单
	 * @param order
	 */
	void createOrder(Order order);
}

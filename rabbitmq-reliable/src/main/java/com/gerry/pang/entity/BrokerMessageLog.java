package com.gerry.pang.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="broker_message_log")
public class BrokerMessageLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String message;
	
	private String messageId;
	
	private Integer tryCount = 0;
	
	private String status;
	
	private Date nextRetry;
	
	private Date createTime;
	
	private Date updateTime;

}

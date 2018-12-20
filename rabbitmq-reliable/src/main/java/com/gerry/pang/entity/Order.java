package com.gerry.pang.entity;

import java.io.Serializable;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="t_order")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;
	
	private String messageId;

}

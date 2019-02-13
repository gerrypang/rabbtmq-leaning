package com.gerry.pang.dto;

public class Order {
	private String id;
	private String name;
	private String content;
	
	@Override
	public String toString() {
		return "Order: { id="+id+", name="+name+", content="+content+" }";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

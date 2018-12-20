package com.gerry.pang.domain;

import lombok.Data;

@Data
public class Student {
	private String name;
	private int age;
	private String sex;
	private String className;
	private Teacher teacher;

}

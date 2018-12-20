package com.gerry.pang.domain;

import lombok.Data;

@Data
public class StudentDTO {
	private String name;
	private int age;
	private String gender;
	private String className;
	private Teacher teacher;

}

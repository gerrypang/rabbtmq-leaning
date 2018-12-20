package com.gerry.pang;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gerry.pang.domain.Student;
import com.gerry.pang.domain.StudentDTO;
import com.gerry.pang.domain.Teacher;

public class BeanCopy {
	
	public void copybean() {
		Teacher a = new Teacher();
		a.setAge(50);
		a.setName("张老师");
		a.setSex("famale");
		Teacher b = new Teacher();
		a.setAge(50);
		a.setName("王老师");
		a.setSex("male");
		Student s1 = new Student();
		s1.setAge(10);
		s1.setClassName("3-1");
		s1.setName("小米");
		s1.setTeacher(a);
		Student s2 = new Student();
		s2.setAge(10);
		s2.setClassName("3-1");
		s2.setName("大米");
		s2.setTeacher(a);
		Student s3 = new Student();
		s3.setAge(10);
		s3.setClassName("3-2");
		s3.setName("小虾");
		s3.setTeacher(a);
		Student s4 = new Student();
		s4.setAge(10);
		s4.setClassName("3-3");
		s4.setName("大咖");
		s4.setTeacher(a);
		List<Student> la = new ArrayList<>();
		List<StudentDTO> lb = new ArrayList<>();
		
		StudentDTO ss1 = new StudentDTO();
		
		la.add(s1);
		la.add(s2);
		la.add(s3);
		la.add(s4);
//		lb = JSON.parseArray(JSON.toJSONString(la), StudentDTO.class);
//		
//		
//		BeanUtils.copyProperties(la, lb);
//		BeanUtils.copyProperties(s1, ss1);
		
//		lb = CopyUtils.deepCopyList(la, StudentDTO.class);
		System.out.println(lb.size());
	}
	
	public static void main(String[] args) {
		new BeanCopy().copybean();
		System.out.println("============");
	}
	
}

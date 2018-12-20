package com.gerry.pang.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CopyUtils {
	
	/**
	 * 浅拷贝集合对象<br>
	 * 
	 * 通过JSON转换String方式
	 * @param fromList
	 * @param toCLass
	 * @return
	 */
	public static <F,T> List<T> shallowCopyList(List<F> fromList, Class<T> toCLass) {
		log.debug("shallowCopyList start");
		List<T> toList = new ArrayList<>();
	    if (CollectionUtils.isEmpty(fromList)) {
	    	log.debug("fromList is empty");
	        return toList;
	    }
	    toList = JSON.parseArray(JSON.toJSONString(fromList), toCLass);
	    log.debug("shallowCopyList finish");
	    return toList;
	}
	
	/**
	 * 深拷贝集合对象<br>
	 * 
	 * 通过Spring.BeanUtils.copyProperties方法
	 * @param fromList
	 * @param toCLass
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <F,T> List<T> deepCopyList(List<F> fromList, Class<T> toCLass) 
			throws InstantiationException, IllegalAccessException {
		return deepCopyList(fromList, toCLass, null);
	}
	
	/**
	 * 深拷贝集合对象<br>
	 * 
	 * 通过Spring.BeanUtils.copyProperties方法
	 * @param fromList
	 * @param toCLass
	 * @param ignoreProperties
	 * @return
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public static <F,T> List<T> deepCopyList(List<F> fromList, Class<T> toCLass, String... ignoreProperties) 
			throws InstantiationException, IllegalAccessException {
		log.debug("deepCopyList start");
		List<T> toList = new ArrayList<>();
		if (CollectionUtils.isEmpty(fromList)) {
			log.debug("fromList is empty");
			return toList;
		}
		for (F from : fromList) {
			T to = deepCopyOne(from, toCLass);
			toList.add(to);
		}
		log.debug("deepCopyList finish");
		return toList;
	}
	
	/**
	 * 深拷贝对象<br>
	 * 
	 * 通过Spring.BeanUtils.copyProperties方法
	 * @param fromList
	 * @param toCLass
	 * @return
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public static<F,T> T deepCopyOne(F from, Class<T> toCLass) 
			throws InstantiationException, IllegalAccessException {
		return deepCopyOne(from, toCLass, null);
	}

	/**
	 * 深拷贝对象<br>
	 * 
	 * 通过Spring.BeanUtils.copyProperties方法
	 * @param fromList
	 * @param toCLass
	 * @param ignoreProperties
	 * @return
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public static<F,T> T deepCopyOne(F from, Class<T> toCLass, String... ignoreProperties) 
			throws InstantiationException, IllegalAccessException {
		log.debug("deepCopyOne start");
		if (from == null || toCLass == null) {
			log.debug("from or toClass is null");
			return null;
		}
		Object to = null;
		try {
			to = toCLass.newInstance();
		} catch (InstantiationException e) {
			log.error("deepCopyOne : 实例化异常", e);
			throw e;
		} catch (IllegalAccessException e) {
			log.error("deepCopyOne : 实例化异常", e);
			throw e;
		}
		BeanUtils.copyProperties(from, to, ignoreProperties);
		log.debug("deepCopyOne finish");
		return (T) to;
	}
}

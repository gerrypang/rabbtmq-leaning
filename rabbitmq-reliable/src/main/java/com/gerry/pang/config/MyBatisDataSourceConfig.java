package com.gerry.pang.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MyBatisDataSourceConfig {

	@Autowired
	private MybatisProperties mybatisProperties;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		// 配置dataSource属性
		sessionFactoryBean.setDataSource(dataSource);
		// 设置mybatis的主配置文件
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactory sqlSessionFactory = null;
		try {
			Resource[] mybatisConfigXml = resolver.getResources(mybatisProperties.getMapperLocations()[0]);
			sessionFactoryBean.setMapperLocations(mybatisConfigXml);
			sqlSessionFactory = sessionFactoryBean.getObject();
			sqlSessionFactory.getConfiguration().setCacheEnabled(true);
		} catch (Exception e) {
			log.error("SqlSessionFactory create error: {}", e);
		}
		return sqlSessionFactory;
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}

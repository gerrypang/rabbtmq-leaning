package com.gerry.pang.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.gerry.pang.common.properties.DruidProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableTransactionManagement
public class DruidDataSourceConfig {

	@Autowired
	private DruidProperties druidProperties;
	
	@Bean
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		try {
			dataSource.setUsername(druidProperties.getUsername());
			dataSource.setPassword(druidProperties.getPassword());
			dataSource.setUrl(druidProperties.getUrl());
			dataSource.setDriverClassName(druidProperties.getDriverClassName());
			
	        dataSource.setInitialSize(druidProperties.getInitialSize());
	        dataSource.setMinIdle(druidProperties.getMinIdle());
	        dataSource.setMaxActive(druidProperties.getMaxActive());
	        dataSource.setTimeBetweenEvictionRunsMillis(druidProperties.getTimeBetweenEvictionRunsMillis());
	        dataSource.setMinEvictableIdleTimeMillis(druidProperties.getMinEvictableIdleTimeMillis());
	        dataSource.setValidationQuery(druidProperties.getValidationQuery());
	        dataSource.setTestWhileIdle(druidProperties.isTestWhileIdle());
	        dataSource.setTestOnBorrow(druidProperties.isTestOnBorrow());
	        dataSource.setTestOnReturn(druidProperties.isTestOnReturn());
	        dataSource.setPoolPreparedStatements(druidProperties.isPoolPreparedStatements());
	        dataSource.setMaxPoolPreparedStatementPerConnectionSize(druidProperties.getMaxPoolPreparedStatementPerConnectionSize());
			dataSource.setFilters(druidProperties.getFilters());
	        dataSource.setConnectionProperties(druidProperties.getConnectionProperties());
	        log.info(" druid datasource config : {} ", dataSource);
		} catch (SQLException e) {
			log.error("druid datasource config error : {} ", e);
		}
		return dataSource;
	}
	
	@Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource(dataSource());
        return txManager;
    }

}

package com.bm.dataservice.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
* @Package com.bm.dataservice.config 
* @Title: MyBatisConfig.java   
* @Description: 数据源配置   
* @author steven  
* @date 2018年8月3日 下午2:52:32
* @version V1.0
 */
@Configuration
public class MyBatisConfig {
	/**
	* Title: dataSource1 
	* Description: TODO
	* @return   
	* DataSource
	* remark : prefix 为 application.properteis中对应属性的前缀
	 */
	@Bean(name = "db1")
	@ConfigurationProperties(prefix = "spring.datasource.db1")
	public DataSource dataSource1() {
		return DataSourceBuilder.create().build();
	}
	/**
	* Title: dataSource2
	* Description: TODO
	* @return   
	* DataSource
	* remark : prefix 为 application.properteis中对应属性的前缀
	 */
	@Bean(name = "db2")
	@ConfigurationProperties(prefix = "spring.datasource.db2")
	public DataSource dataSource2() {
		return DataSourceBuilder.create().build();
	}
}

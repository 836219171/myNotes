package com.bm.dataservice.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
* @Package com.bm.dataservice.config 
* @Title: MyBatisConfigDb1.java   
* @Description: TODO  
* @author steven  
* @date 2018年8月3日 下午3:05:49
* @version V1.0
 */
@Configuration
@MapperScan(basePackages = { "com.bm.dataservice.dao.manager" }, sqlSessionFactoryRef = "sqlSessionFactory1")
public class MyBatisConfigDb1 {
	/**
	 * DataSource
	 */
	@Autowired
	@Qualifier("db1")
	private DataSource ds1;

	/**
	 * MAPPER_LOCATION
	 */
	static final String MAPPER_LOCATION = "classpath:com/bm/dataservice/dao/manager/*.xml";

	/**
	* Title: sqlSessionFactory1 
	* Description: 配置sqlSessionFactory
	* @return
	* @throws Exception   
	* SqlSessionFactory
	 */
	@Bean
	public SqlSessionFactory sqlSessionFactory1() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(ds1);
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.setMapUnderscoreToCamelCase(true);
		factoryBean.setConfiguration(configuration);
		factoryBean.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(MyBatisConfigDb1.MAPPER_LOCATION));
		factoryBean.setConfiguration(configuration);

		return factoryBean.getObject();
	}

	/**
	* Title: sqlSessionTemplate1 
	* Description: 配置SqlSessionTemplate
	* @return
	* @throws Exception   
	* SqlSessionTemplate
	 */
	@Bean
	public SqlSessionTemplate sqlSessionTemplate1() throws Exception {
		// 使用上面配置的Factory
		SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory1());
		return template;
	}


}

package com.bm.dataservice.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
* @Package com.bm.dataservice.config 
* @Title: MyBatisConfigDb2.java   
* @Description: TODO  
* @author steven  
* @date 2018年8月3日 下午3:06:54
* @version V1.0
 */
@Configuration
@MapperScan(basePackages = { "com.bm.dataservice.dao.ads" }, sqlSessionFactoryRef = "sqlSessionFactory2")
public class MyBatisConfigDb2 {
	/**
	 * DataSource
	 */
	@Autowired
	@Qualifier("db2")
	private DataSource ds2;

	/**
	 * MAPPER_LOCATION
	 */
	static final String MAPPER_LOCATION = "classpath:com/bm/dataservice/dao/ads/*.xml";

	/**
	* Title: sqlSessionFactory2 
	* Description: 配置SqlSessionFactory
	* @return
	* @throws Exception   
	* SqlSessionFactory
	 */
	@Bean
	public SqlSessionFactory sqlSessionFactory2() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(ds2);
		factoryBean.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(MyBatisConfigDb2.MAPPER_LOCATION));
		return factoryBean.getObject();
	}

	/**
	* Title: sqlSessionTemplate2 
	* Description: 配置SqlSessionTemplate
	* @return
	* @throws Exception   
	* SqlSessionTemplate
	 */
	@Bean
	public SqlSessionTemplate sqlSessionTemplate2() throws Exception {
		// 使用上面配置的Factory
		SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory2()); 
		return template;
	}

}

package com.bm.dataservice.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
* @Package com.bm.dataservice.config 
* @Title: ServiceConfig.java   
* @Description: 系統配置  
* @author steven  
* @date 2018年7月16日 上午10:52:30
* @version V1.0
 */
@Configuration
@MapperScan("com.bm.dataservice.dao")
public class ServiceConfig {

	/**
	 * Title: buildConfig Description: 跨域处理
	 * 
	 * @return CorsConfiguration
	 */
	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// 1允许任何域名使用
		corsConfiguration.addAllowedOrigin("*"); 
		// 2允许任何头
		corsConfiguration.addAllowedHeader("*"); 
		// 3允许任何方法（post、get等）
		corsConfiguration.addAllowedMethod("*"); 
		return corsConfiguration;
	}

	/**
	 * Title: corsFilter Description: 解决跨域问题
	 * 
	 * @return CorsFilter
	 */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig()); 
		return new CorsFilter(source);
	}

}

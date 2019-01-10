package com.bm.dataservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* @Package com.bm.dataservice.config 
* @Title: SwaggerConfig.java   
* @Description: Swagger配置    
* @author steven  
* @date 2018年7月16日 上午10:53:24
* @version V1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	String basePackge = "com.bm.dataservice.controller";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage(basePackge))
				.paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("数据服务平台  RESTful 接口").description("数据服务平台系统接口").version("1.0.0")
				.termsOfServiceUrl("").license("").licenseUrl("").build();
	}

}

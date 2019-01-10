package com.bm.dataservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
* @Package com.bm.dataservice.config 
* @Title: JwtConfig.java   
* @Description: JWT配置  
* @author steven  
* @date 2018年8月15日 上午10:42:36
* @version V1.0
 */
@Configuration
@ConfigurationProperties(prefix = "bm.dataservice.jwt")
public class JwtConfig {
	/**
	 * JWT 过期时间
	 */
	private Long expire;
	/**
	 * JWT secret
	 */
	private String secret;

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}

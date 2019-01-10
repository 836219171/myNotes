package com.bm.dataservice.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.bm.dataservice.shiro.JwtFilter;
import com.bm.dataservice.shiro.JwtRealm;

/**
* @Package com.bm.dataservice.config 
* @Title: ShiroConfig.java   
* @Description: shiro config  
* @author steven  
* @date 2018年8月14日 下午3:53:10
* @version V1.0
 */
@Configuration
public class ShiroConfig {

	/**
	 * 登录页
	 */
	@Value("${bm.dataservice.page.login}")
	private String loginPage;

	/**
	 * 首页
	 */
	@Value("${bm.dataservice.page.index}")
	private String indexPage;

	/**
	* Title: getManager 
	* Description: securityManager
	* @param realm
	* @return   
	* DefaultWebSecurityManager
	 */




	@Bean("securityManager")
	public DefaultWebSecurityManager getManager(JwtRealm realm) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		// 使用自己的realm
		manager.setRealm(realm);

		/*
		 * 关闭shiro自带的session，详情见文档
		 * http://shiro.apache.org/session-management.html#SessionManagement-
		 * StatelessApplications%28Sessionless%29
		 */
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		manager.setSubjectDAO(subjectDAO);
		return manager;
	}

	/**
	* Title: factory 
	* Description: shiroFilter
	* @param securityManager
	* @return   
	* ShiroFilterFactoryBean
	 */
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean factory(DefaultWebSecurityManager securityManager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

		// 添加自己的过滤器并且取名为jwt
		Map<String, Filter> filterMap = new HashMap<>(16);
		filterMap.put("JwtAuth", new JwtFilter());
		factoryBean.setFilters(filterMap);

		factoryBean.setSecurityManager(securityManager);
		factoryBean.setUnauthorizedUrl(loginPage);
		factoryBean.setLoginUrl(loginPage);
		// SuccessUrl其实在本项目中没有用，本项目中是Controller直接返回Json对象，不涉及到页面跳转
		factoryBean.setSuccessUrl(indexPage);

		/*
		 * 自定义url规则 http://shiro.apache.org/web.html#urls-
		 */
		Map<String, String> filterRuleMap = new HashMap<String, String>(10);
		// 所有请求通过我们自己的JWT Filter
		filterRuleMap.put("/auth/login", "anon");
		filterRuleMap.put("/auth/test", "JwtAuth");
		factoryBean.setFilterChainDefinitionMap(filterRuleMap);
		return factoryBean;
	}

	/**
	* Title: lifecycleBeanPostProcessor 
	* Description: 配置 Bean 后置处理器: 会自动的调用和 Spring 整合后各个组件的生命周期方法
	* @return   
	* LifecycleBeanPostProcessor
	* 【坑】：为什么此处加static ：解决@Value 都不到数据的问题
	*                        https://blog.csdn.net/wuxuyang_7788/article/details/70141812
	 */
	@Bean
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 
	* Title: defaultAdvisorAutoProxyCreator 
	* Description: 1.下面的代码是添加注解支持
	* @return   
	* DefaultAdvisorAutoProxyCreator
	 */
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		// 强制使用cglib，防止重复代理和可能引起代理出错的问题
		// https://zhuanlan.zhihu.com/p/29161098
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
		return defaultAdvisorAutoProxyCreator;
	}

	/**
	* Title: authorizationAttributeSourceAdvisor 
	* Description: 2.下面的代码是添加注解支持
	* @param securityManager
	* @return   
	* AuthorizationAttributeSourceAdvisor
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

}

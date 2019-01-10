package com.atguigu.util;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.atguigu.bean.T_MALL_SKU;
import com.atguigu.service.SearchServiceInf;

/**
@author BY QinLiang
Time:2018Äê5ÔÂ13ÈÕ  
*/
public class GetApplication implements ServletContextListener{

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		SearchServiceInf bean = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getBean(SearchServiceInf.class);
	    
	}


	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}

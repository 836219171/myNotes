package com.bm.dataservice.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.xml.ws.Endpoint;

import com.bm.dataservice.service.remote.MyWebService;

/**
* @Package com.bm.dataservice.config 
* @Title: CxfConfig.java   
* @Description: CXF 配置文件  
* @author steven  
* @date 2018年7月16日 下午3:30:27
* @version V1.0
 */
@Configuration
public class CxfConfig {

	/**
	 * Bus注入
	 */
	@Autowired
    private Bus bus;
	/**
	 * Service注入
	 */
    @Autowired
    private MyWebService myWebService;


    /**
     * 配置发布接口的前置路径 ，默认是加/services
     */
   /* @Bean
    public ServletRegistrationBean dispatcherServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "*//*");
    }*/

    /**
    * Title: endpoint 
    * Description: TODO
    * @return   
    * Endpoint
     */
    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus,myWebService);
      //接口发布在 /DataServices 目录下
        endpoint.publish("/DataServices");
        return endpoint;
    }
}

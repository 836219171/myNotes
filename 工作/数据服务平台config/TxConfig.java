package com.bm.dataservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author xiao liang
 * @version V1.0
 * @Package com.bm.dataservice.config
 * @Title: TxConfig
 * @Description: TODO
 * @date 2018/8/12 12:03
 */
@Configuration
public class TxConfig {


    @Autowired
    @Qualifier("db1")
    private DataSource ds1;


    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(ds1);
    }


}

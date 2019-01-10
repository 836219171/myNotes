package com.bm.dataservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiao liang
 * @version V1.0
 * @Package com.bm.dataservice.config
 * @Title: UpLoadConfig
 * @Description: TODO
 * @date 2018/8/25 11:21
 */
@Configuration
public class UpLoadConfig {

    @Value("${web.upload-path}")
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

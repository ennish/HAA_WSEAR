package com.hkhs.hmms.haa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * cxf服务发布示例
 * @author oKong
 *
 */
@SpringBootApplication
public class HAApp {
    private static Logger logger = LoggerFactory.getLogger(HAApp.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HAApp.class, args);
        logger.info("spirng-boot-cxf-service-chapter34启动!");
    }
}    

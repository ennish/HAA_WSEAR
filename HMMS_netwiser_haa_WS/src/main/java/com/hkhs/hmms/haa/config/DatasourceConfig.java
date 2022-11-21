package com.hkhs.hmms.haa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {
    
    @Profile({"dev","local","test"})
    @Bean("hikariDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource hikariDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Profile({"prd_d2","prd_d8"})
    @Bean("jndiDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource jndiDataSource() {
        return DataSourceBuilder.create().build();
    }

}

package com.hkhs.hmms.haa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiObjectFactoryBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Autowired
    private Environment env;

    private DataSource dataSource;

    @Profile({ "dev", "local", "test" })
    @ConfigurationProperties(prefix = "spring.datasource")
    public void hakiraDataSource() {
        this.dataSource = DataSourceBuilder.create().build();
    }

    @Profile({ "prd_d2", "prd_d8" })
    @ConfigurationProperties(prefix = "spring.datasource")
    public void jndiDataSource() throws IllegalArgumentException, NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:jboss/hmmsDS");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
        bean.afterPropertiesSet();
        this.dataSource = (DataSource) bean.getObject();
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

        } catch (SQLException e) {
            e.printStackTrace();
            // Create connection from jdbc properties
            connection = getConnectionByJdbcProperties(env);
        }
        return connection;
    }

    public Connection getConnectionByJdbcProperties(Environment env) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(env.getProperty("spring.datasource.jdbcUrl"),
                    env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}

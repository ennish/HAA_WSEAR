package com.hkhs.hmms.haa.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AutoConfigureBefore({DataSourceConfig.class})
public class ConnectionTool {
    
    @Autowired
    private DataSource dataSource;
    @Autowired
    private Environment env;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("Fail to get connection from datasource, try to create connection from jdbc properties");
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

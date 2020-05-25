package com.learning.springrest.config;

import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "com.learning.springrest")
@PropertySource({ "classpath:application.properties" })
public class DemoApplicationConfig {

    @Autowired
    private Environment env;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Bean
    public DataSource myDataSource() {
        // for sanity's sake, let's log url and user ... just to make sure we are
        // reading the data
        logger.info("jdbc.url=" + env.getProperty("jdbc.url"));
        logger.info("jdbc.user=" + env.getProperty("jdbc.user"));

        DriverAdapterCPDS driverAdapterCPDS = new DriverAdapterCPDS();

        // set the jdbc driver
        try {
            driverAdapterCPDS.setDriver(env.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // set database connection props
        driverAdapterCPDS.setUrl(env.getProperty("jdbc.url"));
        driverAdapterCPDS.setUser(env.getProperty("jdbc.username"));
        driverAdapterCPDS.setPassword(env.getProperty("jdbc.password"));

        // create connection pool
        SharedPoolDataSource poolDataSource = new SharedPoolDataSource();

        // set connection pool props
        poolDataSource.setConnectionPoolDataSource(driverAdapterCPDS);
        poolDataSource.setMaxTotal(Integer.parseInt(env.getProperty("connection.pool.maxPoolSize")));
        poolDataSource.setDefaultMinIdle(Integer.parseInt(env.getProperty("connection.pool.minPoolSize")));

        return poolDataSource;
    }

    private Properties getHibernateProperties() {
        // set hibernate properties
        Properties props = new Properties();

        props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));

        return props;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        // create session factorys
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        // set the properties
        sessionFactory.setDataSource(myDataSource());
        sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
        sessionFactory.setHibernateProperties(getHibernateProperties());

        return sessionFactory;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {

        // setup transaction manager based on session factory
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }
}
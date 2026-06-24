package com.jarena.config;

import org.apache.commons.dbcp2.BasicDataSource;
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

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.jarena")
@PropertySource("classpath:database.properties")
public class HibernateConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");

        String host     = System.getenv("MYSQLHOST");
        String port     = System.getenv("MYSQLPORT");
        String database = System.getenv("MYSQL_DATABASE") != null
                        ? System.getenv("MYSQL_DATABASE") : System.getenv("MYSQLDATABASE");
        String user     = System.getenv("MYSQLUSER");
        String password = System.getenv("MYSQLPASSWORD");

        if (host != null && !host.isEmpty()) {
            ds.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            ds.setUsername(user);
            ds.setPassword(password);
        } else {
            ds.setUrl(env.getProperty("db.url"));
            ds.setUsername(env.getProperty("db.username"));
            ds.setPassword(env.getProperty("db.password"));
        }

        ds.setInitialSize(5);
        ds.setMaxTotal(20);
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan("com.jarena.model");
        factory.setHibernateProperties(hibernateProperties());
        return factory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect",        env.getProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql",       env.getProperty("hibernate.show_sql"));
        props.setProperty("hibernate.format_sql",     env.getProperty("hibernate.format_sql"));
        props.setProperty("hibernate.hbm2ddl.auto",   env.getProperty("hibernate.hbm2ddl.auto"));
        return props;
    }
}

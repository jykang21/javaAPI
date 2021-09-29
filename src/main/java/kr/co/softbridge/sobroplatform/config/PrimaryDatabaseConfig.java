package kr.co.softbridge.sobroplatform.config;

import com.zaxxer.hikari.HikariDataSource;
import kr.co.softbridge.sobroplatform.commons.annotation.PrimaryMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@MapperScan(value = "kr.co.softbridge.sobroplatform", sqlSessionFactoryRef = "primarySqlSessionFactory", annotationClass = PrimaryMapper.class)
@Configuration
public class PrimaryDatabaseConfig {
	
    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource primaryDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        sqlSessionFactoryBean.setDataSource(primaryDataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath*:mapper/*.xml"));

        SqlSessionFactory factory = sqlSessionFactoryBean.getObject();
        org.apache.ibatis.session.Configuration configuration = factory.getConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCallSettersOnNulls(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);

        return factory;
    }

    @Bean(name = "primarySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate primarySqlSessionTemplate(SqlSessionFactory primarySqlSessionFactory) {
        return new SqlSessionTemplate(primarySqlSessionFactory);
    }

}

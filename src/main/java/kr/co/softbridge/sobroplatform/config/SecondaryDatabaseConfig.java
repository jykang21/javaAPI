package kr.co.softbridge.sobroplatform.config;

import com.zaxxer.hikari.HikariDataSource;
import kr.co.softbridge.sobroplatform.commons.annotation.SecondaryMapper;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@MapperScan(value = "kr.co.softbridge.sobroplatform", sqlSessionFactoryRef = "secondarySqlSessionFactory", annotationClass = SecondaryMapper.class)
@Configuration
public class SecondaryDatabaseConfig {

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "secondary.datasource")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "secondarySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactory(@Qualifier("secondaryDataSource") DataSource secondaryDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        sqlSessionFactoryBean.setDataSource(secondaryDataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath*:mapper/*.xml"));

        SqlSessionFactory factory = sqlSessionFactoryBean.getObject();
        org.apache.ibatis.session.Configuration configuration = factory.getConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCallSettersOnNulls(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "secondarySqlSessionTemplate")
    public SqlSessionTemplate primarySqlSessionTemplate(SqlSessionFactory primarySqlSessionFactory) {
        return new SqlSessionTemplate(primarySqlSessionFactory);
    }

}

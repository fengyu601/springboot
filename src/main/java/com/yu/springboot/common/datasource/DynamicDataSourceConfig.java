package com.yu.springboot.common.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源配置
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-26
 */
@Configuration
@MapperScan(basePackages="com.yu.springboot.dao", sqlSessionFactoryRef = "sqlSessionFactory")
public class DynamicDataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

    //如配置文件中未指定数据源类型，使用该默认值
    private static final Object DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";

    @Autowired
    private Environment environment;

    //注入全局数据源属性Map
    @Resource(name = "generalDSPropertyMap")
    private Map<String, Object> dsPropertyMap;

    private ConversionService conversionService = new DefaultConversionService();

    /**
     * 读取数据源配置属性
     * 注册Bean-数据源属性
     * @return
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean(name = "generalDSPropertyMap")
    protected Map<String, Object> generalDSPropertyMap(){
        Map<String, Object> propertyMap = new HashMap<>();
        return propertyMap;
    }

    /**
     * 读取数据源配置信息
     * 注册Bean-主数据源,随系统启动init
     * @return
     */
    @Primary
    @ConfigurationProperties(prefix = "primary.datasource")
    @Bean(name = "baseDatasource",initMethod = "init",destroyMethod = "close")
    public DataSource baseDatasource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        //绑定通用配置属性
        this.dataBinder(druidDataSource);
        return druidDataSource;
    }

    /**
     * 注册Bean-动态数据源
     * 其管理的数据源在调用时才init
     * @param baseDatasource
     * @return
     */
    @Bean
    public DynamicDataSource dataSource(@Qualifier("baseDatasource")DataSource baseDatasource){
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 添加主数据源
        targetDataSources.put("baseDS", baseDatasource);
        DynamicDataSourceContextHolder.dataSourceKeys.add("baseDS");
        // 添加自定义数据源
        Map<String, DataSource> customDataSources = this.buildCustomDataSources();
        if(customDataSources != null && customDataSources.size() > 0){
            targetDataSources.putAll(customDataSources);
            for (String key : customDataSources.keySet()) {
                DynamicDataSourceContextHolder.dataSourceKeys.add(key);
            }
        }
        //
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        //配置默认数据源
        dataSource.setDefaultTargetDataSource(baseDatasource);
        //
        return dataSource;
    }

    /**
     * sqlSessionFactory
     * @param dataSource
     * @return
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage(environment.getProperty("mybatis.typeAliasesPackage"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(environment.getProperty("mybatis.mapperLocations")));
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 事务管理
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DynamicDataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 读取数据源配置信息
     * 构建多数据源集合
     * @return
     */
    private Map<String, DataSource> buildCustomDataSources(){
        Map<String, DataSource> customDataSources = new HashMap<>();
        //解析数据源配置
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment,"custom.datasource.");
        //获取所有数据源的名称.
        String prefixs = propertyResolver.getProperty("names");
        String[] prefixArr = prefixs.split(",");
        for(String dsPrefix : prefixArr){
            //获取数据源属性
            Map<String, Object> dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
            //Map2DataSource
            DataSource ds = this.buildDataSource(dsMap);
            //绑定通用配置
            dataBinder(ds);
            customDataSources.put(dsPrefix, ds);
        }
        return customDataSources;
    }

    /**
     * 构建数据源对象
     * @param dsMap
     * @return
     */
    private DataSource buildDataSource(Map<String, Object> dsMap) {
        try {
            Object type = dsMap.get("type");
            if (type == null)
                type = DATASOURCE_TYPE_DEFAULT;
            //
            Class<? extends DataSource> dataSourceType;
            dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            //
            String driverClassName = dsMap.get("driverClassName").toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();
            //
            DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username).password(password).type(dataSourceType);
            return factory.build();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 为数据源对象绑定通用配置
     * @param dataSource
     */
    private void dataBinder(DataSource dataSource){
        if(dsPropertyMap == null || dsPropertyMap.size() < 1){
            if(logger.isWarnEnabled()){
                logger.warn("No general properties are configured.");
            }
        }else {
            RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
            //
            dataBinder.setConversionService(conversionService);
            dataBinder.setIgnoreNestedProperties(false);
            dataBinder.setIgnoreInvalidFields(false);
            dataBinder.setIgnoreUnknownFields(true);
            //
            dataBinder.bind(new MutablePropertyValues(dsPropertyMap));
        }
    }
}

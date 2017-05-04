package com.yu.springboot.common.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 注解式指定数据源切面
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-26
 */
/*@Aspect
@Order(-1)// 保证该AOP在@Transactional之前执行
@Component*/
public class DynamicDataSourceAspectM {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspectM.class);

    //@within(com.yu.springboot.common.datasource.TargetDataSource)//只匹配该注解的类
    //@annotation(com.yu.springboot.common.datasource.TargetDataSource)//只匹配该注解的方法
    //@annotation(ds)只匹配该注解的方法, ds可直接参数获取
    //@annotation(ds)和其他不能混用
    @Before("@annotation(ds)")
    public void configDataSource(JoinPoint point, TargetDataSource ds) throws Throwable {
        String dataSourceKey = ds.name();
        if (!DynamicDataSourceContextHolder.containsDataSourceKey(dataSourceKey)) {
            logger.error("DataSource[{}] doesn't exist, use the default DataSource > {}", dataSourceKey, point.getSignature());
        } else {
            DynamicDataSourceContextHolder.setDataSourceKey(dataSourceKey);
            if(logger.isDebugEnabled()){
                logger.debug("DataSource[{}] is being used > {}",dataSourceKey, point.getSignature());
            }
        }
    }

    @After("@annotation(ds)")
    public void revertDataSource(JoinPoint point, TargetDataSource ds) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        if(logger.isDebugEnabled()){
            logger.debug("DataSource[{}] has been cleared > {}", ds.name(), point.getSignature());
        }
    }
}

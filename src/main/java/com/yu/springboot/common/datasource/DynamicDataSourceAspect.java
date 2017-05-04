package com.yu.springboot.common.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 切面-注解式指定数据源
 * @author Feng Yu
 * @version V1.0
 * @date 2017-05-02
 */
@Aspect
@Order(-1)// 保证该AOP在@Transactional之前执行
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * 切点：TargetDataSource注解
     * ----匹配注解类：@within
     * ----匹配注解方法：@annotation
     * 注解子类和父类，所有方法走子类的注解数据源
     * 只注解子类，子类中所有方法走子类指定数据源，其他未Override的父类方法切不到，走默认数据源
     * 只注解父类，只能切到未Override的父类，走父类指定数据源
     */
    @Pointcut("@within(com.yu.springboot.common.datasource.TargetDataSource) || " +
            "@annotation(com.yu.springboot.common.datasource.TargetDataSource) ")
    private void annotationPc(){}

    /**
     * 切点：匹配 GenericServiceImpl 类及其子类中所有方法
     * 注解类 及 注解方法 有效
     */
    @Pointcut("within(com..*.common.base.GenericServiceImpl+)")
    private void withinPc(){}

    /**
     * 前置通知
     * 配置指定数据源
     * @param point
     * @throws Throwable
     */
    @Before("withinPc()")
    public void configDataSource(JoinPoint point) throws Throwable {
        Class<?> targetClass = point.getTarget().getClass();
        Class<?> superClass = targetClass.getSuperclass();
        MethodSignature signature = (MethodSignature)point.getSignature();
        Method method = targetClass.getMethod(signature.getName(), signature.getParameterTypes());
        //方法注解 优先于 当前类注解 优先于 父类注解
        if(method != null && method.isAnnotationPresent(TargetDataSource.class)){
            String dataSourceKey = method.getAnnotation(TargetDataSource.class).name();
            this.setDataSource(dataSourceKey, signature);
        }else if(targetClass.isAnnotationPresent(TargetDataSource.class)){
            String dataSourceKey = targetClass.getAnnotation(TargetDataSource.class).name();
            this.setDataSource(dataSourceKey, signature);
        }else if(superClass != null && superClass.isAnnotationPresent(TargetDataSource.class)){
            String dataSourceKey = superClass.getAnnotation(TargetDataSource.class).name();
            this.setDataSource(dataSourceKey, signature);
        }else{
            if(logger.isInfoEnabled()){
                logger.info("There is no specified dataSource, use the default DataSource. > {}", signature);
            }
        }
    }

    private void setDataSource(String dataSourceKey, Signature signature){
        if (!DynamicDataSourceContextHolder.containsDataSourceKey(dataSourceKey)) {
            logger.warn("DataSource[{}] doesn't exist, use the default DataSource. > {}", dataSourceKey, signature);
        } else {
            DynamicDataSourceContextHolder.setDataSourceKey(dataSourceKey);
            if(logger.isDebugEnabled()){
                logger.debug("DataSource[{}] is being used. > {}",dataSourceKey, signature);
            }
        }
    }

    /**
     * 最终通知
     * 清除指定数据源
     * @param point
     */
    @After("withinPc()")
    public void revertDataSource(JoinPoint point) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        if(logger.isDebugEnabled()){
            logger.debug("DataSource has been cleared. > {}", point.getSignature());
        }
    }
}

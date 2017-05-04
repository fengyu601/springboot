package com.yu.springboot.common.datasource;

import java.lang.annotation.*;

/**
 * 指定数据源
 * 可用于实现类和方法
 * 不要用于接口
 * 注意 Transaction
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-26
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String name() default "";
}
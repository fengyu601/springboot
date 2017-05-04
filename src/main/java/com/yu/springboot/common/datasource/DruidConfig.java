package com.yu.springboot.common.datasource;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.google.common.collect.Lists;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

/**
 * druid配置
 * @see https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-28
 */
@Configuration
public class DruidConfig {

    /**
     * 指定环境下dev 注册DruidFilter
     * @return
     */
    @Bean
    @Profile("dev")
    public FilterRegistrationBean druidFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new WebStatFilter());
        Map<String, String> intParams = new HashMap<>();
        intParams.put("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        registration.setName("DruidWebStatFilter");
        registration.setUrlPatterns(Lists.newArrayList("/*"));
        registration.setInitParameters(intParams);
        return registration;
    }

    /**
     * 指定环境下，注册DruidServlet,配置监控信息显示页面
     * @return
     */
    @Bean
    @Profile("dev")
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(new StatViewServlet());
        registration.setName("druid");
        registration.setUrlMappings(Lists.newArrayList("/druid/*"));
        //自定义添加初始化参数
        Map<String, String> intParams = new HashMap<>();
        intParams.put("loginUsername","cincc");
        intParams.put("loginPassword","cincc123");
        registration.setName("DruidWebStatFilter");
        registration.setInitParameters(intParams);
        return registration;
    }
}

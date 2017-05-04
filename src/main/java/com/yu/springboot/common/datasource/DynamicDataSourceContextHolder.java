package com.yu.springboot.common.datasource;

import java.util.ArrayList;
import java.util.List;

/**
 * dataSourceKey的线程安全容器
 * @author Feng Yu
 * @version V1.0
 * @date 2017-04-26
 */
public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static List<String> dataSourceKeys = new ArrayList<>();

    public static void setDataSourceKey(String dataSourceKey) {
        contextHolder.set(dataSourceKey);
    }

    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    public static void clearDataSourceKey() {
        contextHolder.remove();
    }

    public static boolean containsDataSourceKey(String dataSourceKey){
        return dataSourceKeys.contains(dataSourceKey);
    }
}

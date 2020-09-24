package com.topband.opencvtest.config;


import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 此类 可以静态类中 获取配置数据， 在配置中心中更改，可以动态同步
 * @author ludi
 * @version 1.0
 * @date 2019/10/31 17:35
 * @remark
 */
@Configuration
public class AppConfig {

    public static ConfigurableApplicationContext context;
    public final static String proje_prefix ="opencvtest133"+ "_dev";//项目前缀





    /**
     * 根据key 获取配置
     *
     * @param key
     * @return
     */
    public static String getValueBykey(String key) {
        if (context != null) {
            return context.getEnvironment().getProperty(key, "");
        } else {
            return"";
        }
    }
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.
                config().
                commonTags("application", applicationName);
    }
}

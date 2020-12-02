package com.github.chenxdGit.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;
@Configuration
public class CorsConfig {
	 /**
     * 配置跨域
     * @return
     */
    @Bean
    public CorsWebFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsWebFilter(source);
    }
    
    // 当前跨域请求最大有效时长。这里默认30天
    private long maxAge = 30 * 24 * 60 * 60;
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL); // 1 设置访问源地址
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL); // 2 设置访问源请求头
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL); // 3 设置访问源请求方法
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(maxAge);
        return corsConfiguration;
    }
}

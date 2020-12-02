package com.github.chenxdGit.gateway.config.filter.securityFilter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "jh.gateway.security")
@Data
public class SecurityProperties {
	private Boolean enabled = false;
	private String key;
}

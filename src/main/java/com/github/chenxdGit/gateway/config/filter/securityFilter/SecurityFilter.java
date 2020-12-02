package com.github.chenxdGit.gateway.config.filter.securityFilter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

//@Component
public class SecurityFilter implements GlobalFilter, Ordered {
	 @Override
	    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		 SecurityHttpResponse response = new SecurityHttpResponse (exchange.getResponse());
		 SecurityHttpRequest request = new SecurityHttpRequest(exchange.getRequest());
		 return chain.filter(exchange.mutate().response(response).request(request).build());
	}
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
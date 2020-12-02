package com.github.chenxdGit.gateway.config.filter.logFilter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LogFilter implements GlobalFilter, Ordered {
	 @Override
	    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		 String info = String.format("Method:{%s} Host:{%s} Path:{%s} Query:{%s} Headers:{%s}",
	                exchange.getRequest().getMethod().name(),
	                exchange.getRequest().getURI().getHost(),
	                exchange.getRequest().getURI().getPath(),
	                exchange.getRequest().getQueryParams(), exchange.getRequest().getHeaders());
	        log.info(info);
	        exchange.getAttributes().put("startTime", System.currentTimeMillis());
		 LogHttpResponse response = new LogHttpResponse (exchange.getResponse());
		 LogHttpRequest request = new LogHttpRequest(exchange.getRequest());
		 Mono<Void> filter = chain.filter(exchange.mutate().response(response).request(request).build()).then( Mono.fromRunnable(() -> {
	            Long startTime = exchange.getAttribute("startTime");
	            if (startTime != null) {
	                Long executeTime = (System.currentTimeMillis() - startTime);
	                log.info( exchange.getRequest().getURI().getHost()+""+exchange.getRequest().getURI().getRawPath() + " : " + executeTime + "ms");
	            }
	        }));
		 return filter;
	}
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
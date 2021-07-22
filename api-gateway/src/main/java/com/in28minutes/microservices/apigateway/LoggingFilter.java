package com.in28minutes.microservices.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


import reactor.core.publisher.Mono;

/**
 * @apiNote : It is used to create a separate global loggic for api gateway. Which means whatever request comes to api gateway,
 * it will be traced here and whateever operations that need to be done can be done on it before it's moved to it's specific
 * microservice instance. It can be used for common functions such as authentication, global logging, monitoring/metrics etc.
 * 
 * It is built on top of Spring WebFlux and that's why it is manadatory to use reactive approach of programming.
 * 
 * Please check 162. Step 25 - Implementing Spring Cloud Gateway Logging Filter -> 04:05 for more information.
 * 
 * @author Deepak
 *
 */
@Component
public class LoggingFilter implements GlobalFilter {
	
	private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
	
	/**
	 * @apiNote It is tracing global request api and logging it's path in the console of api gateway.
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		logger.info("Path of the request received -> {}", exchange.getRequest().getPath());
		
		return chain.filter(exchange);
	}

}

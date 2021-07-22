package com.in28minutes.microservices.apigateway;

import java.util.function.Function;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Deepak
 * @apiNote
 * This class helps in designing custom routes in order to use by api gateway.
 * As we saw without this our uri looked like this, http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR. 
 * It is having two names repeating.
 * 
 * Now we have to disable below two properties in properties file in order to use custom routes.
 * spring.cloud.gateway.discovery.locator.enabled=true & spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true
 * 
 * Here we are matching request based on path only (p -> p.path("/get")). We have several other options also such as headers, host, request method etc.
 * 
 * Please check 162. Step 25 - Implementing Spring Cloud Gateway Logging Filter -> 04:05 for more information.
 */
@Configuration
public class ApiGatewayConfiguration {
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		
		Function<PredicateSpec, Buildable<Route>> routeFunction = 
				   p -> p.path("/get")
				  .filters(f -> f.addRequestHeader("MyHeader", "MyUri")
						  .addRequestParameter("Param", "MyValue"))
				  .uri("http://httpbin.org:80");
				   
	   Function<PredicateSpec, Buildable<Route>> routeFunctionCurrencyExchange = 
			   p -> p.path("/currency-exchange/**") // It is using predicate with regex. It's checking your new custom url after localhost and port, /currency-exchange/[rest of the service].
			  .uri("lb://currency-exchange"); //lb: means load balancer. It's the service name as shown in load balancer instances list. 
			   								//Whenever any request which comes for or matches "/currency-exchange/**" in the loadbalancer instances list,
			   								 // It will redirect to currency-exchange microservice instance.
			   								//After redirecting to that particular microservice instance, it checks wheather that particular service with that exact url present or not, after removing api gateway's localhost and port.
			   								//old url: http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR
			   								//Custom new url: http://localhost:8765/currency-exchange/from/USD/to/INR
			   								//It's the first currency-exchange in old uri which is getting matched using regex.
			   								//In the new uri this part /currency-exchange/from/USD/to/INR is used to check if there is any service with that uri or not.
			   								//If there is no actual service with the new service url in your microservice instance
			   								//then use filter with rewritePath as shown in routeFunctionCurrencyConversionNew.
			   
	   Function<PredicateSpec, Buildable<Route>> routeFunctionCurrencyConversion = 
			   p -> p.path("/currency-conversion/**") // It is using predicate with regex. It's checking your new custom url after localhost and port, /currency-conversion/[rest of the service].
			  .uri("lb://currency-conversion"); //lb: means load balancer. It's the service name as shown in load balancer instances list. 
			   									//Whenever any request which comes for or matches "/currency-conversion/**" in the loadbalancer instances list,
			   									// It will redirect to currency-conversion microservice instance.
			   									//Rest of the process is same as explained in routeFunctionCurrencyExchange
					   
	   Function<PredicateSpec, Buildable<Route>> routeFunctionCurrencyConversionFeign = 
			   p -> p.path("/currency-conversion-feign/**") // It is using predicate with regex. It's checking your new custom url after localhost and port, /currency-conversion-feign/[rest of the service].
			  .uri("lb://currency-conversion"); 	//lb: means load balancer. It's the service name as shown in load balancer instances list.
			   										//Whenever any request which comes for or matches "/currency-conversion-feign/**" in the loadbalancer instances list,
			   											// It will redirect to currency-conversion microservice instance.
			   										//Rest of the process is same as explained in routeFunctionCurrencyExchange
			   
	   Function<PredicateSpec, Buildable<Route>> routeFunctionCurrencyConversionNew = 
			   p -> p.path("/currency-conversion-new/**") // It is using predicate with regex. It's checking your new custom url after localhost and port, /currency-conversion-new/[rest of the service].
			  .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
					  					"/currency-conversion-feign/${segment}"))// Now instead of checking new version of uri after reaching that particular microservice intance, it will check -feign versino as the uri is rewritten now
			   .uri("lb://currency-conversion"); 	//lb: means load balancer. It's the service name as shown in load balancer instances list.
			  										//Whenever any request which comes for or matches "/currency-conversion-new/**" in the loadbalancer instances list, 
			   											// It will redirect to currency-exchange microservice instance.
			   										//Rest of the process is same as explained in routeFunctionCurrencyExchange
			   
	   Function<PredicateSpec, Buildable<Route>> routeFunctionLimitsService = 
			   p -> p.path("/limits-service/**") // It is using predicate with regex. It's checking your new custom url after localhost and port, /limits-service/[rest of the service].
			  .uri("lb://limits-service"); 	//lb: means load balancer. It's the service name as shown in load balancer instances list.
			   										//Whenever any request which comes for or matches "/limits-service/**" in the loadbalancer instances list,
			   											// It will redirect to currency-exchange microservice instance.
			   										//Rest of the process is same as explained in routeFunctionCurrencyExchange
			   
		return builder.routes()
				.route(routeFunction)
				.route(routeFunctionCurrencyExchange)
				.route(routeFunctionCurrencyConversion)
				.route(routeFunctionCurrencyConversionFeign)
				.route(routeFunctionCurrencyConversionNew)
				.route(routeFunctionLimitsService)
				.build();
	}
}

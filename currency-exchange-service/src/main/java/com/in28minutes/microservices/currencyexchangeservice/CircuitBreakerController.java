package com.in28minutes.microservices.currencyexchangeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {
	
	private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
	
	/**
	 * @apiNote The second arg of @Retry is method name which is written below this method.
	 * 
	 * fallbackMethod is called, if in case when even after maxRetryAttempts still api isn't giving any
	 * response back, in that case fallbackMethod will be called and appropriate method will be called.
	 * We can send any reponse type. Here in this hardCodedResponse method we are sending plain String value.
	 * However, in place of plain String or text we even send JSON response.
	 * @return
	 */
	@GetMapping("/sample-api")
//	@Retry(name="default")
	@Retry(name="sample-api", fallbackMethod = "hardCodedResponse") // it's configured in applicaton.properties
	@CircuitBreaker(name="default", fallbackMethod = "hardCodedResponse")
	@Bulkhead(name = "sample-api")
	//Rate Limiting means that in 10 seconds we will only allow 10000 calls to our api
//	@RateLimiter(name = "default") // It is shown separtely in sampleApiRateLimiter method.
	public String sampleApi() {
		logger.info("Sample API call Received");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
//		return "Sample API";
		return forEntity.getBody();
	}
	
	@GetMapping("/sample-api-rate-limiter")
	@RateLimiter(name = "default")
	@Bulkhead(name = "default") // It means the number of concurrent calls to the api are allowed
	public String sampleApiRateLimiter() {
		logger.info("Sample API call Received sample-api-rate-limiter");
		return "Sample-api-rate-limiter";
	}
	
	/**This is the custom method which is used to send some custom response along with the 
	 * exception happening because of which program is getting retried.
	 * For that it require Throwable as it's argument, which means we can have any exception
	 * (as all the exception has Throwable as it's parent super class). And whenever that exception
	 * happens that method with it's exception and message will be shown as a fallback response.
	 * */
	public String hardCodedResponse(Exception ex) {
		return "fallback-response";
	}

}

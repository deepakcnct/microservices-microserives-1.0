package com.in28minutes.microservices.currencyexchangeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CurrencyExchangeServiceApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(CurrencyExchangeServiceApplication.class, args);
		} catch (Throwable e) {
			if(e.getClass().getName().contains("SilentExitException")) {
//	            LOGGER.debug("");
				System.out.println("Spring is restarting the main thread - See spring-boot-devtools");
	        } else {
//	            LOGGER.error("", e);
	        	System.out.println("Application crashed!");
	        }
		}
	}

}

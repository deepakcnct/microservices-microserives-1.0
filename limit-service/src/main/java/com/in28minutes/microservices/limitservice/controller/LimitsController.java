package com.in28minutes.microservices.limitservice.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in28minutes.microservices.limitservice.beans.Limits;
import com.in28minutes.microservices.limitservice.configuiration.Configuration;

@RestController
public class LimitsController {
	
	@Autowired
	private Configuration configuration;
	
	@GetMapping("/limits-service")
	public Limits reterieveLimits() {
		return new Limits(configuration.getMinimum(),configuration.getMaximum());
	}
}

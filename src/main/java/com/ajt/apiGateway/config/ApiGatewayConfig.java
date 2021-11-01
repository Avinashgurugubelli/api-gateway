package com.ajt.apiGateway.config;

import com.ajt.apiGateway.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApiGatewayConfig {

	@Autowired
	private JwtAuthenticationFilter filter;

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		// Note: uri value: i am using EUREKA load balancing instead of host and port to access the microservices, The name can be identified from eureka dashboard (Instance -> application)
		return builder.routes()
				.route("auth", r -> r.path("/api/auth/**").filters(f -> f.filter(filter)).uri("lb://AUTHSERVICE"))
				.route("employeeService", r -> r.path("/api/ems/**").filters(f -> f.filter(filter)).uri("lb://EMPLOYEEMANAGEMENTSERVICE")
				).build();
	}
}

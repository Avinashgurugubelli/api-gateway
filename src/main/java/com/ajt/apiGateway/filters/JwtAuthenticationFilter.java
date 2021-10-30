package com.ajt.apiGateway.filters;

import com.ajt.apiGateway.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

	private final List<String> publicApiPaths = new ArrayList<String>() {{
		add("/register");
		add("/login");
	}};

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		Predicate<ServerHttpRequest> isSecured = this.isSecuredApi();
		if (isSecured.test(request)) {
			if (!request.getHeaders().containsKey("Authorization")) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}
			final String token = request.getHeaders().getOrEmpty("Authorization").get(0);
			Boolean isValidToken = this.jwtUtil.validateJwtToken(token);
			if (!isValidToken) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				return response.setComplete();
			}
		}
		return chain.filter(exchange);
	}

	private Predicate<ServerHttpRequest> isSecuredApi() {
		return r -> publicApiPaths.stream()
				.noneMatch(uri -> r.getURI().getPath().contains(uri));
	}
}

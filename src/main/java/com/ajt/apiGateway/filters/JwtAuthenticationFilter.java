package com.ajt.apiGateway.filters;

import com.ajt.apiGateway.exception.ApiError;
import com.ajt.apiGateway.exception.ApiException;
import com.ajt.apiGateway.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

	ObjectMapper objectMapper = new ObjectMapper();

	private final List<String> publicApiPaths = new ArrayList<String>() {{
		add("/auth/register");
		add("/auth/login");
		add("/auth/refresh-token");
		add("/auth/logout");
	}};

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		Predicate<ServerHttpRequest> isSecured = this.isSecuredApi();
		if (isSecured.test(request)) {
			Mono<Void> validateStatus = this.validateAuthHeader(exchange);
			if (validateStatus != null) {
				return validateStatus;
			}
			String token = request.getHeaders().getOrEmpty("Authorization").get(0);
			token = this.extractJwtToken(token);
			if (token != null) {
				try {
					this.jwtUtil.validateJwtToken(token);
				} catch (ApiException ex) {
					return this.onError(exchange, ex.getMessage(), ex.getApiError().type, null);
				}
			}
		}
		return chain.filter(exchange);
	}

	private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus, Throwable cause) {
		ApiError apiError = new ApiError(errorMessage, httpStatus, httpStatus.value());
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		try {
			byte[] messageBytes = this.objectMapper.writeValueAsBytes(apiError);
			DataBuffer dataBuffer = response.bufferFactory().wrap(messageBytes);
			return response.writeWith(Mono.just(dataBuffer));
		} catch (JsonProcessingException ex) {
			return response.setComplete();
		}
	}

	private Predicate<ServerHttpRequest> isSecuredApi() {
		return r -> publicApiPaths.stream()
				.noneMatch(uri -> r.getURI().getPath().contains(uri));
	}

	private String extractJwtToken(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return null;
	}

	private Mono<Void> validateAuthHeader(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		if (!request.getHeaders().containsKey("Authorization")) {
			return this.onError(exchange, "Authorization header missing in the request", HttpStatus.BAD_REQUEST, null);
		} else {
			String token = request.getHeaders().getOrEmpty("Authorization").get(0);
			if (token == null || !token.startsWith("Bearer ")) {
				return this.onError(exchange, "Invalid auth token", HttpStatus.BAD_REQUEST, null);
			}
		}
		return null;
	}
}

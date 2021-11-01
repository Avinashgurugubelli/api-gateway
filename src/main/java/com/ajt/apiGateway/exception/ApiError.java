package com.ajt.apiGateway.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

@JsonPropertyOrder({
		"code", "type", "message"
})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiError {
	public Integer code = null;
	public HttpStatus type = null;
	public String message = null;

	public ApiError() {
	}

	public ApiError(String message, HttpStatus type, Integer code) {
		this.message = message;
		this.type = type;
		this.code = code;
	}
}

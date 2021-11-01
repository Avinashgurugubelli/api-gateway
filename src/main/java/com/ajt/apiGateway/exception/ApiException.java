package com.ajt.apiGateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
	private ApiError apiError = new ApiError();


	/**
	 * suppress the stack trace
	 * @return
	 */
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
		this.apiError.message = message;
		this.apiError.type = HttpStatus.INTERNAL_SERVER_ERROR;
		this.apiError.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

	public ApiException(String message, HttpStatus httpStatus, Throwable cause) {
		super(message, cause);
		this.apiError.message = message;
		this.apiError.type = httpStatus;
		this.apiError.code = httpStatus.value();
	}
}

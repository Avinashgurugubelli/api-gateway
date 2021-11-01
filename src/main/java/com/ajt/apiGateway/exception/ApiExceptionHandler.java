package com.ajt.apiGateway.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(value = {ApiException.class})
	public ApiError handleApiReqException(ApiException ex) {
		ApiError apiError = new ApiError();
		// setting up api exception to return to end user
		apiError.message = ex.getApiError().message;
		apiError.code = ex.getApiError().code;
		apiError.type = ex.getApiError().type;
		return apiError;
	}
}

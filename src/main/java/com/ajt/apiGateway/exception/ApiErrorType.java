package com.ajt.apiGateway.exception;


import org.springframework.lang.Nullable;

public enum ApiErrorType {
	BAD_REQUEST(400, "BAD_REQUEST"),
	AUTHENTICATION_ERROR(401, "AUTHENTICATION_ERROR"),
	INTERNAL_SERVER_ERROR (500, "INTERNAL_SERVER_ERROR"),
	JMS_ERROR(501, "JMS_ERROR"),
	SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE"),
	API_CALLING_ERROR(504, "API_CALLING_ERROR");

	private final int value;

	private final String reasonPhrase;

	public int value() {
		return this.value;
	}

	/**
	 * Return the reason phrase of this status code.
	 */
	public String getReasonPhrase() {
		return this.reasonPhrase;
	}

	ApiErrorType(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	@Override
	public String toString() {
		return this.value + " " + name();
	}

	public static ApiErrorType valueOf(int statusCode) {
		ApiErrorType status = resolve(statusCode);
		if (status == null) {
			throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
		}
		return status;
	}

	@Nullable
	public static ApiErrorType resolve(int statusCode) {
		for (ApiErrorType status : values()) {
			if (status.value == statusCode) {
				return status;
			}
		}
		return null;
	}
}

package com.ajt.apiGateway.jwt;

import com.ajt.apiGateway.exception.ApiException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	/**
	 * This secret key should be as same as the auth service jwtSecret
	 * So this should ideally be configured through Spring Cloud Bus so that any change to this value will be reflected in all other services
	 */
	@Value("${ajt.app.jwtSecret}")
	private String jwtSecret;


	public Claims getClaims(final String token) {
		try {
			Claims body = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
			return body;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			throw new ApiException("Invalid JWT signature: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
		} catch (MalformedJwtException e) {
			throw new ApiException("Invalid JWT token: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
		} catch (ExpiredJwtException e) {
			throw new ApiException("JWT token is expired: " + e.getMessage(), HttpStatus.UNAUTHORIZED, null);
		} catch (UnsupportedJwtException e) {
			throw new ApiException("JWT token is unsupported: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
		} catch (IllegalArgumentException e) {
			throw new ApiException("JWT claims string is empty: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
		} catch (Exception e) {
			throw new ApiException("Some error occurred while validating JWT token: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
		}
	}
}

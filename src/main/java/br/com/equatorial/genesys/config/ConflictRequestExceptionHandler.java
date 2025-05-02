package br.com.equatorial.genesys.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ConflictRequestExceptionHandler implements ExceptionMapper<ConflictRequestException> {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public Response toResponse(ConflictRequestException exception) {
		try {
			return Response.status(Response.Status.CONFLICT)
					.entity(mapper.writeValueAsString(new ErrorResponse(exception.getMessage()))).build();
		} catch (JsonProcessingException e) {}

		return Response.status(Response.Status.CONFLICT).build();
	}

	public static class ErrorResponse {
		private String message;

		public ErrorResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

}

package br.com.equatorial.genesys.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Provider
public class BadRequestExceptionHandler implements  ExceptionMapper<BadRequestException> {
	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public Response toResponse(BadRequestException exception) {
		try {
			return Response.status(Response.Status.BAD_REQUEST)
			        .entity(mapper.writeValueAsString(new ErrorResponse(exception.getMessage())))
			        .build();
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
		
		return Response.status(Response.Status.BAD_REQUEST).build();
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

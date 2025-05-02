package br.com.equatorial.genesys.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {
	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public Response toResponse(NotFoundException exception) {
		try {
			if(exception instanceof NotFoundException) {
				return Response.status(Response.Status.NOT_FOUND)
				        .entity(mapper.writeValueAsString(new ErrorResponse(exception.getMessage())))
				        .build();
			}else {
				return Response.status(Response.Status.BAD_REQUEST)
				        .entity(mapper.writeValueAsString(new ErrorResponse(exception.getMessage())))
				        .build();
			}
		} catch (JsonProcessingException e) {}
		
		return Response.status(Response.Status.NOT_FOUND).build();
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

package br.com.equatorial.genesys.config;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ConflictRequestException extends WebApplicationException {

	private static final long serialVersionUID = 400414159965565383L;

	public ConflictRequestException(String message) {
		super(Response.status(Response.Status.CONFLICT).entity(message).build());
	}

}

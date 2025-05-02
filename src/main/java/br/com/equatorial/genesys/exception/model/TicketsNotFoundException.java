package br.com.equatorial.genesys.exception.model;

import jakarta.ws.rs.NotFoundException;

public class TicketsNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 2340837188401117783L;
	
	public TicketsNotFoundException(String message) {
		super(message);
	}

}

package br.com.equatorial.genesys.resource.impl;

import br.com.equatorial.genesys.resource.UserResource;
import br.com.equatorial.genesys.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserResourceImpl implements UserResource {
	
	private final UserService service;

	@Override
	public Response lerUsers() {
		return Response.ok(service.listarUsuarios()).build();
	}

}

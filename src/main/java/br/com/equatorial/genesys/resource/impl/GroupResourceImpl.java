package br.com.equatorial.genesys.resource.impl;

import br.com.equatorial.genesys.resource.GroupResource;
import br.com.equatorial.genesys.service.GroupService;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GroupResourceImpl implements GroupResource {

	private final GroupService service;
	
	@Override
	public Response listAll() {
		return Response.ok(service.listAll()).build();
	}

}

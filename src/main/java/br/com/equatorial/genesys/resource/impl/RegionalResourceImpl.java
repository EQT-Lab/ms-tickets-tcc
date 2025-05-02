package br.com.equatorial.genesys.resource.impl;

import br.com.equatorial.genesys.resource.RegionalResource;
import br.com.equatorial.genesys.service.RegionalService;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;



@AllArgsConstructor
public class RegionalResourceImpl implements RegionalResource{
	
	private final RegionalService service;

	@Override
	public Response lerRegionais() {
		return Response.ok(service.listarRegionais()).build();
	}

}

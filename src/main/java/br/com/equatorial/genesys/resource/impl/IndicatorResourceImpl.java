package br.com.equatorial.genesys.resource.impl;

import org.apache.http.HttpStatus;

import br.com.equatorial.genesys.model.Indicator;
import br.com.equatorial.genesys.resource.IndicatorResource;
import br.com.equatorial.genesys.service.IndicatorService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndicatorResourceImpl implements IndicatorResource {

	private final IndicatorService service;
	
	@Override
	public Response lerIndicadores() {
		return Response.ok(service.listarIndicators()).build();
	}

	@Override
	public Response atualizarIndicador(Long indicadorId, Indicator indicator) {
		return Response.status(HttpStatus.SC_NO_CONTENT).entity(service.atualizarIndicator(indicadorId, indicator)).build();
	}

}

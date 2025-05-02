package br.com.equatorial.genesys.resource.impl;

import br.com.equatorial.genesys.resource.AgenciaResource;
import br.com.equatorial.genesys.service.AgenciaService;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AgenciaResourceImpl implements AgenciaResource {

    private final AgenciaService service;


    @Override
    public Response listarAgencias(Long regionalId) {
        return Response.ok(service.listarAgencias(regionalId)).build();
    }
}

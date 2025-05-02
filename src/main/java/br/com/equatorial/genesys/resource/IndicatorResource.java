package br.com.equatorial.genesys.resource;


import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.equatorial.genesys.model.Indicator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/indicators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Indicator", description = "Operações relacionadas a Indicator")
public interface IndicatorResource {
	
	@GET
	@Operation(summary = "Obter todos os indicadores")
	@APIResponse(responseCode = "200", description = "Indicadores encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Indicator[].class)))
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	public Response lerIndicadores();
	
	
	@PUT
	@Path("/{id}")
	@Operation(summary = "Atualizar um indicador por ID")
	@APIResponse(responseCode = "200", description = "Indicador atualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Indicator.class)))
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	public Response atualizarIndicador(@PathParam("id") Long id, @RequestBody Indicator indicator);
	
	

}

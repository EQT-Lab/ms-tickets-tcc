package br.com.equatorial.genesys.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.equatorial.genesys.model.Regional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/regionais")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Regional", description = "Operações relacionadas a Regional")
public interface RegionalResource {
	
	
	
	@GET
	@Operation(summary = "Obter todos os regionais")
	@APIResponse(responseCode = "200", description = "Regionais encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Regional[].class)))
	@APIResponse(responseCode = "404", description = "Não encontrado")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	public Response lerRegionais();

}

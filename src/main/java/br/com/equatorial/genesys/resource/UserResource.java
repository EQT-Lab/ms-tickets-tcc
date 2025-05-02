package br.com.equatorial.genesys.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.equatorial.genesys.model.Users;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User", description = "Operações relacionadas a User")
public interface UserResource {

	
	
	@GET
	@Operation(summary = "Obter todos os users")
	@APIResponse(responseCode = "200", description = "Users encontrados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users[].class)))
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	public Response lerUsers();
}

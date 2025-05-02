package br.com.equatorial.genesys.resource;

import br.com.equatorial.genesys.model.Agencia;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/agencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Agencias", description = "Operações relacionadas a agências")
public interface AgenciaResource {

    @GET
    @Operation(summary = "Obter as agências e postos com base na regional")
    @APIResponse(responseCode = "200", description = "Agências encontradas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Agencia[].class)))
    @APIResponse(responseCode = "404", description = "Não encontrado!")
    @APIResponse(responseCode = "500", description = "Erro interno no servidor")
    public Response listarAgencias(@QueryParam("regionalId") Long regionalId);
}

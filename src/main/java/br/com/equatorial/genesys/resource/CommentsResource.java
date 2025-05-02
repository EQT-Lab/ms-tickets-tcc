package br.com.equatorial.genesys.resource;


import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.equatorial.genesys.request.dto.CommentsRequestDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/comments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Comments", description = "Operações relacionadas a Comments")
public interface CommentsResource {
	
	
	@POST
	@Operation(summary = "Cria um novo Comments", description = "Cria um novo Comments e retorna o status de sucesso.")
	@APIResponse(responseCode = "201", description = "Comments criado com sucesso")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response criarComments(CommentsRequestDto comments);
	
	
	@GET
	@Path(value = "/ticket/{ticketId}")	
	@Operation(summary = "Lista todos os Comments por Ticket ID", description = "Retorna uma lista de todos os Comments por Ticket.")
	@APIResponse(responseCode = "200", description = "Lista de Comments", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listarCommentsByTicketId(Long ticketId);
	
	
	@GET
	@Path(value = "/user/{userId}")	
	@Operation(summary = "Lista todos os Comments por User ID", description = "Retorna uma lista de todos os Comments por User.")
	@APIResponse(responseCode = "200", description = "Lista de Comments", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listarCommentsByUserId(Long userId);
	
}

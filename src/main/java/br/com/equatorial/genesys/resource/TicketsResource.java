package br.com.equatorial.genesys.resource;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.request.dto.TicketCloseRequestDto;
import br.com.equatorial.genesys.request.dto.TicketRequestDto;
import br.com.equatorial.genesys.request.dto.TicketUpdateRequestDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tickets", description = "Operações relacionadas a tickets")
public interface TicketsResource {

	@POST
	@Operation(summary = "Cria um novo Ticket", description = "Cria um novo Ticket e retorna o status de criação.")
	@APIResponse(responseCode = "201", description = "Ticket criado com sucesso")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response criarTicket(@Parameter(description = "Objeto Ticket a ser criado", required = true) TicketRequestDto ticket);

	@GET
	@Path("/{id}")
	@Operation(summary = "Lê um Ticket por ID", description = "Recupera um Ticket pelo ID fornecido.")
	@APIResponse(responseCode = "200", description = "Ticket encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)))
	@APIResponse(responseCode = "404", description = "Ticket não encontrado")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response lerTicket(
			@Parameter(description = "ID do Ticket a ser lido", required = true) @PathParam("id") Long id);

	@GET
	@Operation(summary = "Lista todos os Tickets", description = "Retorna uma lista de todos os Tickets.")
	@APIResponse(responseCode = "200", description = "Lista de Tickets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listarTickets(@QueryParam("dt_creation_start") String dtCreationStart, @QueryParam("dt_creation_end") String dtCreationEnd,
								  @QueryParam("status") String status, @QueryParam("module") Long module,
								  @QueryParam("assigned") String assigned, @QueryParam("forwarded") String forwarded,
								  @QueryParam("createdby") String createdby, @QueryParam("mytickets") String myTickets,
								  @Parameter(description = "Email do usuário no header", required = true) @HeaderParam("X-User-Id") String xUserId,
								  @QueryParam("dt_closed_start")String dtClosedStart, @QueryParam("dt_closed_end")String dtClosedEnd);


	@PUT
	@Path("/{id}/forwardings")
	@Operation(summary = "Atualiza um Ticket", description = "Atualiza as informações de um Ticket existente.")
	@APIResponse(responseCode = "200", description = "Ticket atualizado com sucesso")
	@APIResponse(responseCode = "404", description = "Ticket não encontrado")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response atualizarEncaminhamento(
			@Parameter(description = "ID do Ticket a ser atualizado", required = true) @PathParam("id") Long id,
			@Parameter(description = "Objeto Ticket com as novas informações", required = true) TicketUpdateRequestDto ticket,
			@Parameter(description = "Email do usuário no header", required = true) @HeaderParam("X-User-Id") String xUserId);
	@PUT
	@Path("/{id}/assignments")
	@Operation(summary = "Atualiza um Ticket", description = "Atualiza as informações de um Ticket existente.")
	@APIResponse(responseCode = "200", description = "Ticket atualizado com sucesso")
	@APIResponse(responseCode = "404", description = "Ticket não encontrado")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response atualizarAtribuicao(
			@Parameter(description = "ID do Ticket a ser atualizado", required = true) @PathParam("id") Long id,
			@Parameter(description = "Objeto Ticket com as novas informações", required = true) TicketUpdateRequestDto ticket,
			@Parameter(description = "Email do usuário no header", required = true) @HeaderParam("X-User-Id") String xUserId);
	
	@PUT
	@Path("/close/{id}")
	@Operation(summary = "Conclui um Ticket", description = "Conclui um Ticket existente.")
	@APIResponse(responseCode = "204", description = "Ticket concluido com sucesso")
	@APIResponse(responseCode = "404", description = "Ticket não encontrado")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response fecharTicket(
			@Parameter(description = "ID do Ticket a ser concluido", required = true) @PathParam("id") Long id,
			@Parameter(description = "Objeto Ticket a ser criado", required = true) TicketCloseRequestDto ticket);


	@GET
	@Path("/sla")
	@Operation(summary = "Checa o sla de todos os tickets", description = "Checa o sla de todos os tickets em aberto, atualiza status e envia notificações")
	@APIResponse(responseCode = "200", description = "Status atualizados com sucesso")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response atualizarSla();
	
	
	@POST
	@Path("/externalId")
	@Operation(summary = "Retorna todos os externalId", description = "Retorna todos os externalId")
	@APIResponse(responseCode = "200", description = "External ID encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long[].class)))
	@APIResponse(responseCode = "404", description = "External ID não encontrado")
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<String> lerTicketByExternalId(List<String> externalId);
	
	
	@GET
	@Path("/pagination")
	@Operation(summary = "Retornar os tickets paginados", description = "Retorna os tickets paginados")
	@APIResponse(responseCode = "200", description = "Lista de Tickets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
	@APIResponse(responseCode = "500", description = "Erro interno no servidor")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listarTickets(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size);

}

package br.com.equatorial.genesys.resource;


import br.com.equatorial.genesys.request.dto.DirectMailRequestDto;
import br.com.equatorial.genesys.service.EmailService;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.equatorial.genesys.model.Notifications;
import br.com.equatorial.genesys.service.NotificationsService;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Notifications", description = "Operações relacionadas a Notifications")
@RequiredArgsConstructor
public class NotificationsResource {
	
	private final NotificationsService service;
	private final EmailService emailService;
	
	@GET
	@Operation(summary = "Buscar todas as notificações do Usuário", description = "Recupera as notifications por userEmail.")
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "List of notifications", 
						 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notifications.class))),
			@APIResponse(responseCode = "500", description = "Server error")
	})
	public Response listAll(@Parameter(description = "Email do usuário no header", required = true) @HeaderParam("X-User-Id") String userEmail) {
		return Response.ok(service.findBydUserEmail(userEmail)).build();
	}
	
	@PUT
	@Operation(summary= "Marcar notificação como lida", description = "Marcar notificação como lida.")
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "List of notifications", 
						 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notifications.class))),
			@APIResponse(responseCode = "500", description = "Server error")
	})
	public Response update(@Parameter(description = "Email do usuário no header", required = true) @HeaderParam("X-User-Id") String userEmail) {
		service.update(userEmail);
		return Response.ok().build();
	}
	
	@PUT
	@Path("/{id}")
	@Operation(summary="Marcar notificação como lida por ID", description = "Marcar notificação como lida por ID.")
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "List of notifications", 
					 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notifications.class))),
		@APIResponse(responseCode = "500", description = "Server error")
	})
	public Response updateById(@Parameter(description = "ID da notificação", required = true) @HeaderParam("X-User-Id") String userEmail,
			@PathParam("id") Long id) {
		service.updateById(userEmail, id);
		return Response.ok().build();
	}
	
	@DELETE
	@Operation(summary= "Deletar todas as notificações do user", description = "Deletar todas as notificações do user.")
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "List of notifications", 
						 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notifications.class))),
			@APIResponse(responseCode = "500", description = "Server error")
	})
	public Response delete(@Parameter(description = "Email do usuário no header", required = true) @HeaderParam("X-User-Id") String userEmail) {
		service.delete(userEmail);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{id}")
	@Operation(summary="Deletar notificação por ID", description = "Deletar notificação  por ID.")
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "List of notifications", 
					 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notifications.class))),
		@APIResponse(responseCode = "500", description = "Server error")
	})
	public Response deleteById(@Parameter(description = "ID da notificação", required = true) @HeaderParam("X-User-Id") String userEmail,
			@PathParam("id") Long id) {
		service.deleteById(userEmail, id);
		return Response.ok().build();
	}

	@POST
	@Path("/directmail")
	@Operation(summary = "Envio direto de email", description = "Envio direto dos dados por emails para uso em dev")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendDirectMail(DirectMailRequestDto request){
		emailService.directMail(request.getTargetMail(), request.getMessage(), request.getSubject(), request.getId());
		return Response.ok("Email enviado com sucesso").build();
	}

}

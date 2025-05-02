package br.com.equatorial.genesys.resource.impl;

import br.com.equatorial.genesys.request.dto.CommentsRequestDto;
import br.com.equatorial.genesys.resource.CommentsResource;
import br.com.equatorial.genesys.service.CommentsService;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentsResourceImpl implements CommentsResource {

	private final CommentsService service;
	
	@Override
	public Response criarComments(CommentsRequestDto comments) {
		return Response.status(Response.Status.CREATED).entity(service.criarComments(comments)).build();
	}

	@Override
	public Response listarCommentsByTicketId(Long ticketId) {
		return Response.ok(service.listarCommentsByTicketId(ticketId)).build();
	}

	@Override
	public Response listarCommentsByUserId(Long userId) {
		return Response.ok(service.listarCommentsByUserId(userId)).build();
	}

}

package br.com.equatorial.genesys.resource.impl;


import java.io.IOException;
import java.util.List;

import br.com.equatorial.genesys.request.dto.TicketCloseRequestDto;
import br.com.equatorial.genesys.request.dto.TicketRequestDto;
import br.com.equatorial.genesys.request.dto.TicketUpdateRequestDto;
import br.com.equatorial.genesys.resource.TicketsResource;
import br.com.equatorial.genesys.service.TicketsService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class TicketsResourceImpl implements TicketsResource {
	
    private final TicketsService ticketService;

    public Response criarTicket(TicketRequestDto ticketRequest) {
        return Response.status(Response.Status.CREATED).entity(ticketService.criarTicket(ticketRequest)).build();
    }

    public Response lerTicket(Long id) {
        return Response.ok(ticketService.lerTicket(id)).build();
    }

    public Response listarTickets(String dtCreationStart, String dtCreationEnd, String status, Long module, String assigned, String forwarded,
                                  String createdby, String myTickets, String xUserId, String dtClosedStart, String dtClosedEnd) {
        if(null == xUserId) {
            throw new BadRequestException("Header X-User-Id não informado");
        }

        return Response.ok(ticketService.listarTickets(dtCreationStart, dtCreationEnd, status, module, assigned, forwarded, createdby,
                myTickets, xUserId, dtClosedStart, dtClosedEnd)).build();
    }

    public Response atualizarEncaminhamento(Long id, TicketUpdateRequestDto ticket, String xUserId) {		
    	if(null == xUserId) {
    		throw new BadRequestException("Header X-User-Id não informado");
		}
    	
        ticketService.atualizarEncaminhamento(id, ticket, xUserId);
        return Response.noContent().build();
    }
    
    public Response atualizarAtribuicao(Long id, TicketUpdateRequestDto ticket, String xUserId) {		
    	if(null == xUserId) {
    		throw new BadRequestException("Header X-User-Id não informado");
    	}
    	
    	ticketService.atualizarAtribuicao(id, ticket, xUserId);
    	return Response.noContent().build();
    }

	public Response fecharTicket(Long id, TicketCloseRequestDto ticket) {
		ticketService.fecharTicket(id, ticket);
		return Response.status(Response.Status.NO_CONTENT).build();
	}

    public Response atualizarSla(){
        return Response.ok(ticketService.atualizarSla()).build();
    }

	@Override
	public List<String> lerTicketByExternalId(List<String> externalId) {
		return ticketService.listAllByExternalId(externalId);
	}

	@Override
	public Response listarTickets(int page, int size) {
		return Response.ok(ticketService.buscaPaginada(page, size)).build();
	}

}


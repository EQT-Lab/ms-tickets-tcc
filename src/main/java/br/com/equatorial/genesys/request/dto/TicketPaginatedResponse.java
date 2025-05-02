package br.com.equatorial.genesys.request.dto;

import java.util.List;

import br.com.equatorial.genesys.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketPaginatedResponse {
	
	private List<Ticket> data;
	
	private Pagination pagination;

}

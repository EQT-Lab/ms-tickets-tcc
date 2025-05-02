package br.com.equatorial.genesys.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateRequestDto {
	
	private TicketAssignmentRequestDto assignments;
	private TicketForwardingRequestDto forwardings;
	
	private String description;
	
}

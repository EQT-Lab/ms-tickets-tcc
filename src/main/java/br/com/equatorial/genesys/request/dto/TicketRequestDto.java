package br.com.equatorial.genesys.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDto {
	
	private String user;
	private String email;
	private Long distributor;
	private Long regional;
	private Long indicator;
	private String description;
	private Long moduleId;
	private String externalId;
	
	private TicketAssignmentRequestDto assignments;
	private TicketForwardingRequestDto forwardings;
	private List<IndicatorRequestDto> indicatorDataSummary;
	
	private Object atendimentoPresencialModalData;
	
}

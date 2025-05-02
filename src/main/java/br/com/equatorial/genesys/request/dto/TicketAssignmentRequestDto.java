package br.com.equatorial.genesys.request.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssignmentRequestDto {
	
	private List<Long> usersId;
	
	private List<Long> groupsId;
	
	private String description;

}

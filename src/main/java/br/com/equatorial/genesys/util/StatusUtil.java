package br.com.equatorial.genesys.util;

import java.util.Map;

import br.com.equatorial.genesys.enums.TicketStatus;

public class StatusUtil {
	
	public static Map<String, Integer> statusPriority = Map.of(TicketStatus.NOVO.getDescricao(), 1, 
    		TicketStatus.ANALISE.getDescricao(), 2,
    		TicketStatus.PENDENTE.getDescricao(), 3,
    		TicketStatus.SLA.getDescricao(), 4,
    		TicketStatus.CONCLUIDO.getDescricao(), 5);

}

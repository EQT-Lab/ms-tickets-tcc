package br.com.equatorial.genesys.service;

import java.util.List;

import br.com.equatorial.genesys.model.IndicatorTicket;
import br.com.equatorial.genesys.repository.IndicatorTicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class IndicatorTicketService {

	private final IndicatorTicketRepository repository;
	
	public void create(IndicatorTicket indicatorTicket) {
		log.info("Criando relacionamento entre Ticket e Indicador");
		repository.persist(indicatorTicket);
	}

	public List<IndicatorTicket> findByTicketId(Long id) {
		return repository.findByTicketId(id);
	}

}

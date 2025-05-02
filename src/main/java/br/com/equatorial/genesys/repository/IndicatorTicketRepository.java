package br.com.equatorial.genesys.repository;

import java.util.List;

import br.com.equatorial.genesys.model.IndicatorTicket;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;

@Singleton
public class IndicatorTicketRepository implements PanacheRepository<IndicatorTicket> {

	public List<IndicatorTicket> findByTicketId(Long ticketId) {
		return list("ticket.id", ticketId);
	}

}

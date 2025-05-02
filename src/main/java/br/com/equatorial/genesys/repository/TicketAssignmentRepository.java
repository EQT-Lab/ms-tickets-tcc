package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.TicketAssignment;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;

@Singleton
public class TicketAssignmentRepository implements PanacheRepository<TicketAssignment> {

	public PanacheQuery<TicketAssignment> findByTicket(Ticket ticket) {
		return find("ticket", ticket);
	}

}

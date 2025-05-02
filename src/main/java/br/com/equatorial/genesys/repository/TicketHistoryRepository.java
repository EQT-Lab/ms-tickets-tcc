package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.TicketHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;

@Singleton
public class TicketHistoryRepository implements PanacheRepository<TicketHistory> {

}

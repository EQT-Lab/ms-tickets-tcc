package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.TicketForwarding;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;

@Singleton
public class TicketForwardingRepository implements PanacheRepository<TicketForwarding> {

}

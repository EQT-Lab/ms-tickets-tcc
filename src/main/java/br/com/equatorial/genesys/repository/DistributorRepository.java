package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Distributor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;


@Singleton
public class DistributorRepository implements PanacheRepository<Distributor> {
	
	public Distributor findByState(String state) {
		return find("state", state).firstResult();
	}
}


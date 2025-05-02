package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Regional;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;

@Singleton
public class RegionalRepository implements PanacheRepository<Regional> {
	
}


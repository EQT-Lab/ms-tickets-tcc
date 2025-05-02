package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Modulo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;


@Singleton
public class ModuleRepository implements PanacheRepository<Modulo> {
	
}


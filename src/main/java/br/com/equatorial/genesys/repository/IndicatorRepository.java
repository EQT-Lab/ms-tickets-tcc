package br.com.equatorial.genesys.repository;

import java.util.List;

import br.com.equatorial.genesys.model.Indicator;
import br.com.equatorial.genesys.model.Modulo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;


@Singleton
public class IndicatorRepository implements PanacheRepository<Indicator> {

	public Indicator findByName(String name) {
		Indicator firstResult = find("name", name).firstResult();
		return firstResult;
	}

	public List<Indicator> findByModuleId(Long moduleId) {
		return find("module", Modulo.builder().id(moduleId).build()).list();
	}

	
}


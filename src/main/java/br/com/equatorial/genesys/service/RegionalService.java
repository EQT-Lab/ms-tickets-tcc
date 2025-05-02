package br.com.equatorial.genesys.service;

import java.util.List;

import br.com.equatorial.genesys.model.Regional;
import br.com.equatorial.genesys.repository.RegionalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class RegionalService {

	private final RegionalRepository repository;

	public List<Regional> listarRegionais() {
		log.info("Obtendo Todos os Regionais");
		return repository.findAll().list();
	}

	public Regional lerRegional(Long id) {
		log.info("Obtendo Regional: {}", id);
		
		Regional regional = repository.findById(id);
		
		if(null == regional) {
			log.error("Regional por ID {} não encontrado", id);
			throw new NotFoundException("Regional não encontrado");
		}
		
		return regional;
	}

	
	@Transactional
	public Regional criarRegional(Regional regional) {
		log.info("Criando Regional: {}", regional.getName());
		repository.persist(regional);
		return regional;
	}

	@Transactional
	public Regional atualizarRegional(Long id, Regional regional) {
		log.info("Atualizando Regional: {}", regional);
		Regional regionalToUpdate = lerRegional(id);

		if (null != regional.getName()) {
			if (!regional.getName().equals(regionalToUpdate.getName())) {
				regionalToUpdate.setName(regional.getName());
			}
		}

		if (null != regional.getDistributor()) {
			if (regionalToUpdate.getDistributor() != regional.getDistributor()) {
				regionalToUpdate.setDistributor(regional.getDistributor());
			}
		}

		return regionalToUpdate;
	}

}

package br.com.equatorial.genesys.service;

import java.util.List;

import br.com.equatorial.genesys.model.Distributor;
import br.com.equatorial.genesys.repository.DistributorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class DistributorService {

	private final DistributorRepository repository;

	@Transactional
	public Distributor criarDistributor(Distributor distributor) {
		log.info("Criando Distribuidor: {}", distributor);
		repository.persist(distributor);
		return distributor;
	}

	public Distributor lerDistributor(Long id) {
		log.info("Lendo Distribuidor: {}", id);
		
		Distributor distributor = repository.findById(id);
		
		if(null == distributor) {
			log.error("Distribuidor por ID {} não encontrado", id);
			throw new NotFoundException("Distribuidor não encontrado");
		}
		
		return distributor;
	}

	public List<Distributor> listarDistributors() {
		log.info("Listando todos os Distribuidores");
		return repository.findAll().list();
	}
	
	public Distributor lerDistributorByState(String state) {
		log.info("Lendo Distribuidor por Estado: {}", state);
		
		Distributor distributor = repository.findByState(state);
		if(null == distributor) {
			log.error("Distribuidor por State {} não encontrado", state);
			throw new NotFoundException("Distribuidor não encontrado");
		}
		return distributor;
	}

}

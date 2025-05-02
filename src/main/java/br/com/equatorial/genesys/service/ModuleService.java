package br.com.equatorial.genesys.service;

import java.util.List;

import br.com.equatorial.genesys.model.Modulo;
import br.com.equatorial.genesys.repository.ModuleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class ModuleService {

	private final ModuleRepository repository;
	
	public List<Modulo> listarModules() {
		log.info("Obtendo Todos os Modulos");
		return repository.findAll().list();
	}
	
	public Modulo lerModule(Long id) {
		log.info("Obtendo Modulo: {}", id);
		
		Modulo module = repository.findById(id);
		
		if(null == module) {
			log.error("Modulo por ID {} não encontrado", id);
			throw new NotFoundException("Modulo não encontrado");
		}
		
		return module;
	}
	
	
	@Transactional
	public Modulo criarModule(Modulo module) {
		log.info("Criando Modulo: {}", module.getName());
		repository.persist(module);
		return module;
	}
	
	@Transactional
	public Modulo atualizarModule(Long id, Modulo module) {
		log.info("Atualizando Modulo: {}", module.getName());
		Modulo moduleToUpdate = lerModule(id);
		if(module.getName() != null) {
			moduleToUpdate.setName(module.getName());
		}
		return moduleToUpdate;
	}
	
}

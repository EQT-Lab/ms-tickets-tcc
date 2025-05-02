package br.com.equatorial.genesys.service;

import java.util.List;

import br.com.equatorial.genesys.enums.IndicatorMapping;
import br.com.equatorial.genesys.model.Indicator;
import br.com.equatorial.genesys.repository.IndicatorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class IndicatorService {

	private final IndicatorRepository repository;
	
	@Transactional
    public Indicator criarIndicator(Indicator indicator) {
		log.info("Criando Indicador: {}", indicator.getName());
    	repository.persist(indicator);
    	return indicator;
    }

    public Indicator lerIndicator(Long id) {
    	log.info("Obtendo Indicador: {}", id);
		 Indicator indicator = repository.findById(id);
		 
		 if(null == indicator) {
			 log.error("Indicador por ID {} não encontrado", id);
			 throw new NotFoundException("Indicador não encontrado");
		 }
		 
		 return indicator;
    }

    public List<Indicator> listarIndicators() {
    	log.info("Obtendo Todos os Indicadores");
    	return repository.findAll().list();
    }
    
    public Indicator lerIndicatorByName(String name) {
    	
		log.info("Obtendo Indicador por Name: {}", name);
		
		Indicator indicator = repository.findByName(name);
		if(null == indicator) {
			log.error("Indicador por Name {} não encontrado", name);
			throw new NotFoundException("Indicador não encontrado");
		}
		return indicator;
	}

    @Transactional
    public Indicator atualizarIndicator(Long id, Indicator indicator) {
    	log.info("Atualizando Indicador: {}", indicator.getName());
    	Indicator indicatorToUpdate = lerIndicator(id);

		if(indicator.getName() != null) {
			indicatorToUpdate.setName(indicator.getName());
		}
		
		if(indicator.getSla() != null) {
			if(indicator.getSla() != indicatorToUpdate.getSla()) {
				indicatorToUpdate.setSla(indicator.getSla());
			}
			
		}
		
		return indicatorToUpdate;
		
    }
    
    public List<Indicator> findIndicatorByModuleId(Long moduleId){
    	log.info("Obtendo Indicadores por ModuleId: {}", moduleId);
		return repository.findByModuleId(moduleId);
    }
    
    public Indicator findByIndicatorIdAndModuleId(Long indicatorId, Long moduleId) {
		log.info("Obtendo Indicador por IndicatorId e ModuleId: {} e {}", indicatorId, moduleId);
		String name = IndicatorMapping.getNameByIds(moduleId.intValue(), indicatorId.intValue());
		return this.lerIndicatorByName(name);
	}
    
    public Indicator findByIndicatorNameAndModuleId(String name, Long moduleId) {
		log.info("Obtendo Indicador por Name e ModuleId: {} e {}", name, moduleId);
		String nameIndicator = IndicatorMapping.getName(moduleId.intValue(), name);
		return this.lerIndicatorByName(nameIndicator);
	}

}


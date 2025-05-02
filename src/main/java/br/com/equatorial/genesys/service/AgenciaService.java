package br.com.equatorial.genesys.service;

import br.com.equatorial.genesys.model.Agencia;
import br.com.equatorial.genesys.repository.AgenciaRepository;
import br.com.equatorial.genesys.request.dto.AgenciaRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class AgenciaService {

    private final AgenciaRepository repository;

    public List<AgenciaRequestDto> listarAgencias(Long regionalId){

        log.info("Listando agências impostas da regional de id {}", regionalId);

//        if (regionalId == null){
//            throw new NotFoundException("Regional não informada");
//        }

        log.info("Aplicando filtros e realizando consulta");
        List<Agencia> list = repository.findWithFilters(regionalId).list();
        log.info("Total de agências e postos encontrados {}", list.size());

        list.sort(Comparator.comparing(Agencia::getName));

        //Encaplusamento em Dto
        List<AgenciaRequestDto> dtoList = new ArrayList<>();
        for (Agencia agencia : list){
            AgenciaRequestDto agenciaDto = new AgenciaRequestDto();
            agenciaDto.setId(agencia.getId());
            agenciaDto.setName(agencia.getName());
            agenciaDto.setType(agencia.getType());
            agenciaDto.setRegionalId(agencia.getRegional().getId());

            dtoList.add(agenciaDto);
        }

        return dtoList;
    }
}

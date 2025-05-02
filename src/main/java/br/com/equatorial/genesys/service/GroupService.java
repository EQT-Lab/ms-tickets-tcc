package br.com.equatorial.genesys.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.equatorial.genesys.model.Group;
import br.com.equatorial.genesys.repository.GroupRepository;
import br.com.equatorial.genesys.response.dto.GroupResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    
    public List<GroupResponseDto> listAll() {
    	
    	log.info("Listando todos os grupos");
    	
		return groupRepository.listAll()
				.stream()
				.map(group -> new GroupResponseDto(group.getId(), group.getName()))
                .collect(Collectors.toList());
    }
    
    public Group findById(Long id) {
		Group group = groupRepository.findById(id);
		
		if(null == group) {
			log.error("Group por ID {} não encontrado", id);
			throw new NotFoundException("Group por ID " + id + " não encontrado");
		}
		
		return group;
	}
    

}

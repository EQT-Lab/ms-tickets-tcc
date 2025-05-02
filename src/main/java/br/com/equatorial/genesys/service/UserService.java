package br.com.equatorial.genesys.service;

import java.time.LocalDateTime;
import java.util.List;

import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class UserService {

	private final UserRepository repository;

	@Transactional
	public Users criarUsuario(Users user) {
		log.info("Criando User: {}", user.getName());
		repository.persist(user);
		return user;
	}

	public Users lerUsuario(Long id) {
		log.info("Buscando User pelo ID: {}", id);
		Users byId = repository.findById(id);

		if(null == byId) {
			log.error("User por ID {} não encontrado", id);
			throw new NotFoundException("User não encontrado");
		}

		return byId;
	}

	public List<Users> listarUsuarios() {
		log.info("Buscando todos os Users");
		List<Users> users = repository.findAll().list();
		users.removeIf(user -> user.getId() == 0);
		return users;
	}


	public Users lerUsuarioByName(String name) {
		log.info("Buscando User pelo nome: {}", name);
		Users byName = repository.findByName(name);
		
		if(null == byName) {
			log.error("User por nome {} não encontrado", name);
			throw new NotFoundException("User por nome " + name + " não encontrado");
		}
		
		return byName;
	}

	public Users lerUsuarioByEmail(String email) {
		log.info("Buscando User pelo email: {}", email);
		Users byEmail = repository.findByEmail(email);

		if(null == byEmail) {
			log.error("User por email {} não encontrado", email);
			throw new NotFoundException("User por email " + email + " não encontrado");
		}

		return byEmail;
	}
	
	public Users lerUsuarioByUserAd(String userAd) {
		log.info("Buscando User pelo userAd: {}", userAd);
		Users byUserAd = repository.findByEmail(userAd);
		
//		if(null == byUserAd) {
//			log.error("User por userAd {} não encontrado", userAd);
//			throw new NotFoundException("User por userAd " + userAd + " não encontrado");
//		}
		
		return byUserAd;
	}

	@Transactional
	public Users atualizarUsuario(Long id, Users user) {
		log.info("Atualizando User: {}", user.getName());
		
		Users userToUpdate = lerUsuario(id);
		
		if(user.getName() != null) {
			userToUpdate.setName(user.getName());
		}
		
		if(user.getEmail() != null) {
			userToUpdate.setEmail(user.getEmail());
		}
		
//		if(user.getUserAd() != null) {
//			userToUpdate.setUserAd(user.getUserAd());
//		}
		
		userToUpdate.setUpdated(LocalDateTime.now());
		
		return userToUpdate;
	}
	
	public Users identificarUser(String userAd, String userEmail) {
		
		Users user = null;
		try {
			user = lerUsuario(Long.valueOf(userAd));
		}catch(NumberFormatException e) {
			
			user = lerUsuarioByUserAd(userAd);
		}
		
		if(user == null) {
			user = lerUsuarioByEmail(userEmail);
			
			if(user == null) {
				user = new Users();
				user.setEmail(userEmail);
				user.setName(extrairNome(userEmail));
			}
			repository.persist(user);
		}
		
		
		return user;
	}
	
	private String extrairNome(String email) {

		StringBuilder nomeCompleto = new StringBuilder();
		
        String beforeAt = email.substring(0, email.indexOf("@"));

        // Separa o nome e sobrenome com base no "."
        String[] nameParts = beforeAt.split("\\.");

        // Verifica se há nome e sobrenome
        if (nameParts.length >= 2) {
        	  String firstName = capitalizeFirstLetter(nameParts[0]);
              String lastName = capitalizeFirstLetter(nameParts[1]);
            
            nomeCompleto.append(firstName).append(" ").append(lastName);
        } else {
           log.error("Formato de e-mail inválido para extração de nome e sobrenome.");
           nomeCompleto.append(beforeAt);
        }
        
        return nomeCompleto.toString();
	}
	
	 private static String capitalizeFirstLetter(String word) {
	        if (word == null || word.isEmpty()) {
	            return word;
	        }
	        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
	    }
}

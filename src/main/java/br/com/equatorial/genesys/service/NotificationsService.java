package br.com.equatorial.genesys.service;

import java.util.List;

import br.com.equatorial.genesys.model.Notifications;
import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.repository.NotificationsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class NotificationsService {
	
	private final NotificationsRepository repository;
	
	public List<Notifications> findBydUserEmail(String userEmail) {
		
		if(null == userEmail) {
			log.error("Header X-User-Id não foi informado");
			throw new BadRequestException("Header X-User-Id não foi informado");
		}
		log.info("Listando Notifications do User: {}", userEmail);
		List<Notifications> list = repository.findByUserEmail(userEmail).list();
		
		return list;
	}

	public void save(Users user, String createdBy, String ticketName, String descriptions) {
		log.info("Salvando Notification do User: {}", user.getName());
		Notifications notifications = new Notifications();
		notifications.setCreatedBy(createdBy);
		notifications.setTicketName(ticketName);
		notifications.setDescription(descriptions);
		notifications.setUser(user);
		repository.persist(notifications);
	}

	public void update(String userEmail) {
		log.info("Atualizando todas as notificacoes do User: {}", userEmail);
		repository.setAllReadByEmail(userEmail);
	}

	@Transactional
	public void updateById(String userEmail, Long id) {
		log.info("Atualizando a notificacao {} do User {}", id, userEmail);
		Notifications notification = repository.findByIdAndEmail(id, userEmail).firstResult();
		
		if(null != notification) {
			if(notification.isActive()) {
				notification.setActive(false);
			}else {
				notification.setActive(true);
			}
		}
	}

	public void delete(String userEmail) {
		log.info("Deletando todas as notificacoes do User: {}", userEmail);
		repository.deleteAllByEmail(userEmail);
	}

	public void deleteById(String userEmail, Long id) {
		log.info("Deletando a notificacao {} do User {}", id, userEmail);
		repository.deleteAllById(id, userEmail);
	}

}

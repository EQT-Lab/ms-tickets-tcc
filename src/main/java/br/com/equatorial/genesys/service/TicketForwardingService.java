package br.com.equatorial.genesys.service;

import java.util.ArrayList;
import java.util.List;

import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.TicketForwarding;
import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.repository.TicketForwardingRepository;
import br.com.equatorial.genesys.request.dto.TicketForwardingRequestDto;
import br.com.equatorial.genesys.request.dto.TicketRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class TicketForwardingService {
	
	private final TicketForwardingRepository repository;
	
	private final UserService userService;
	private final ModuleService moduloService;
	private final GroupService groupService;
	
	private final NotificationsService notificationsService;
	
	@Transactional
	public void create(Ticket ticket, TicketRequestDto ticketRequest) {
		log.info("Encaminhando o ticket {}, para usuarios e modulos.", ticket.getDescription());

		if(null != ticketRequest.getForwardings()) {
			if(null != ticketRequest.getForwardings().getUsersId()) {
				
				for(Long id : ticketRequest.getForwardings().getUsersId()) {
					TicketForwarding ticketForwarding = new TicketForwarding();
					ticketForwarding.setTicket(ticket);
					ticketForwarding.setUser(userService.lerUsuario(id));
		
					repository.persist(ticketForwarding);
					notificationsService.save(ticketForwarding.getUser(), ticket.getUser().getName(), ticket.getIndicator().getName(), ticketRequest.getDescription());
				}
			}
			
			if(null != ticketRequest.getForwardings().getModulesId()) {
				for(Long id : ticketRequest.getForwardings().getModulesId()) {
					TicketForwarding ticketForwarding = new TicketForwarding();
					ticketForwarding.setTicket(ticket);
					ticketForwarding.setModule(moduloService.lerModule(id));
		
					repository.persist(ticketForwarding);
				}
			}
			
			if(null != ticketRequest.getForwardings().getGroupsId()) {
				for(Long id : ticketRequest.getForwardings().getGroupsId()) {
					TicketForwarding ticketForwarding = new TicketForwarding();
					ticketForwarding.setTicket(ticket);
					ticketForwarding.setGroup(groupService.findById(id));
		
					repository.persist(ticketForwarding);
				}
			}
			
		}
	}
	
	public boolean addeUser(Ticket ticket, TicketForwardingRequestDto ticketForwarding) {
		log.info("Atribuindo o encaminhamento do ticket {}, para usuarios e modulos.", ticket.getDescription());
		boolean updated = false;
		if(null != ticketForwarding.getUsersId() || null != ticketForwarding.getModulesId()) {
			
			updated = insertForwarding(ticket, ticketForwarding, updated);
		}
		
		return updated;
	}
	
	
	@Transactional
	public boolean update(Ticket ticket, TicketForwardingRequestDto ticketForwarding) {
		
		log.info("Atribuindo o encaminhamento do ticket {}, para usuarios e modulos.", ticket.getDescription());
		boolean updated = false;
		if(null != ticketForwarding.getUsersId() || null != ticketForwarding.getModulesId()) {
			
			repository.delete("ticket", ticket);
			updated = insertForwarding(ticket, ticketForwarding, updated);
		}
		
		return updated;
	}

	private boolean insertForwarding(Ticket ticket, TicketForwardingRequestDto ticketForwarding, boolean updated) {
		if(null != ticketForwarding.getUsersId()) {
			
			for(Long userId: ticketForwarding.getUsersId()) {
				TicketForwarding forwarding = new TicketForwarding();
				forwarding.setTicket(ticket);
				forwarding.setUser(userService.lerUsuario(userId));
				repository.persist(forwarding);
				notificationsService.save(forwarding.getUser(), ticket.getUser().getName(), ticket.getIndicator().getName(), ticketForwarding.getDescription());
				updated = true;
			}
		}
		
		if(null != ticketForwarding.getModulesId()) {
			
			for(Long moduleId: ticketForwarding.getModulesId()) {
				TicketForwarding forwarding = new TicketForwarding();
				forwarding.setTicket(ticket);
				forwarding.setModule(moduloService.lerModule(moduleId));
				repository.persist(forwarding);
				updated = true;
			}
		}
		
		if(null != ticketForwarding.getGroupsId()) {
			
			for(Long groupId: ticketForwarding.getGroupsId()) {
				TicketForwarding forwarding = new TicketForwarding();
				forwarding.setTicket(ticket);
				forwarding.setGroup(groupService.findById(groupId));
				repository.persist(forwarding);
				updated = true;
			}
		}
		return updated;
	}
	
	public String forwardingText(Users user, Ticket ticket, TicketForwardingRequestDto forwardings) {
		StringBuilder details = new StringBuilder(" Encaminhado para: ");
		
		StringBuilder users = new StringBuilder();
		if (null != forwardings.getUsersId()) {

			List<String> userNames = new ArrayList<>();
			forwardings.getUsersId().stream()
					.forEach(id -> userNames.add(userService.lerUsuario(Long.valueOf(id)).getName()));

			users.append(String.join(", ", userNames));
			details.append(users);

		}

		StringBuilder groups = new StringBuilder();
		if(null != forwardings.getGroupsId()) {
			List<String> groupNames = new ArrayList<>();
			forwardings.getGroupsId().stream()
					.forEach(id -> groupNames.add(groupService.findById(Long.valueOf(id)).getName()));
			
			groups.append(String.join(", ", groupNames));

			if(!users.isEmpty()) {
				details.append(", ");
			}
			details.append(groups);
		}
		
		
		if(details.length() >= 20) {
			return details.toString();
		}
		
		return "";
	}
	
}

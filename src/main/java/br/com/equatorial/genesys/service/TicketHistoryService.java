package br.com.equatorial.genesys.service;

import java.util.ArrayList;
import java.util.List;

import br.com.equatorial.genesys.enums.HistoryTypes;
import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.TicketHistory;
import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.repository.TicketHistoryRepository;
import br.com.equatorial.genesys.repository.UserRepository;
import br.com.equatorial.genesys.request.dto.TicketForwardingRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class TicketHistoryService {
	
	private final TicketHistoryRepository repository;

	private final UserService userService;
	private final GroupService groupService;

	private final UserRepository userRepository;

	public void create(Ticket ticket) {
		StringBuilder action = new StringBuilder(ticket.getUser().getName())
				.append(" Criou um ticket sobre ")
				.append(ticket.getIndicator().getName()).append(".");
		
		log.info("Criando historico de criação de Ticket -> {}", action);
		
		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setTicket(ticket);
		ticketHistory.setUser(ticket.getUser());
		ticketHistory.setAction(action.toString());
		ticketHistory.setType(HistoryTypes.CREATION.getDescricao());
		ticketHistory.setDetails(ticket.getDescription());
		
		repository.persist(ticketHistory);
	}
	
	public void createHistoryToComment(Ticket ticket, Users user, String comment) {
		StringBuilder action = new StringBuilder(user.getName())
				.append(" Comentou o ticket ")
				.append(ticket.getIndicator().getName()).append(".");
		
		log.info("Criando historico de atualização de Ticket -> {}", action);
		
		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setTicket(ticket);
		ticketHistory.setUser(user);
		ticketHistory.setAction(action.toString());
		ticketHistory.setType(HistoryTypes.COMMENT.getDescricao());
		ticketHistory.setDetails(comment);
		
		repository.persist(ticketHistory);
	}
	
	public void updateField(Ticket ticket, String fieldName, String fieldValue) {
		StringBuilder action = new StringBuilder(ticket.getUser().getName())
				.append(" Fez alterações no ticket ")
				.append(ticket.getIndicator().getName()).append(".");
		
		log.info("Criando historico de atualização de Ticket -> {}", action);
		
		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setTicket(ticket);
		ticketHistory.setUser(ticket.getUser());
		ticketHistory.setAction(action.toString());
		ticketHistory.setType(HistoryTypes.UPDATE.getDescricao());
		ticketHistory.setDetails(fieldValue);
		
		repository.persist(ticketHistory);
	}
	
	public void closeTicket(Ticket ticket, Users user) {
		log.info("Criando historico de fechamento de Ticket");
		
		StringBuilder action = new StringBuilder().append(user.getName())
				.append(" Concluiu o ticket ")
				.append(ticket.getIndicator().getName()).append(".");
		
		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setTicket(ticket);
		ticketHistory.setUser(user);
		ticketHistory.setAction(action.toString());
		ticketHistory.setType(HistoryTypes.CLOSED.getDescricao());
		ticketHistory.setDetails("status");
		
		repository.persist(ticketHistory);
	}
	
	public void atribuitionTicket(Users user, Ticket ticket, String description, String text) {
		log.info("Criando historico de atribuição de Ticket");
		
		
		StringBuilder action = new StringBuilder().append(user.getName())
				.append(" fez alterações no ticket ").append(ticket.getIndicator().getName());

		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setTicket(ticket);
		ticketHistory.setUser(user);
		ticketHistory.setAction(action.toString());
		ticketHistory.setType(HistoryTypes.UPDATE.getDescricao());
		ticketHistory.setDetails(text);
		ticketHistory.setDescription(description);
		
		repository.persist(ticketHistory);
	}
	
	
	public void forwardingTicket(Users user, Ticket ticket, TicketForwardingRequestDto forwardings) {
		log.info("Criando historico de encaminhamento de Ticket");
		
		
		StringBuilder action = new StringBuilder().append(user.getName())
				.append(" Encaminhou o ticket ")
				.append(ticket.getIndicator().getName()).append(".");
		

		StringBuilder details = new StringBuilder("Para ");
		StringBuilder users = new StringBuilder();
		if(null != forwardings.getUsersId()) {
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
		
		
		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setTicket(ticket);
		ticketHistory.setUser(user);
		ticketHistory.setAction(action.toString());
		ticketHistory.setType(HistoryTypes.FORWARDING.getDescricao());
		ticketHistory.setDetails(details.toString());
		ticketHistory.setDescription(forwardings.getDescription());
		
		repository.persist(ticketHistory);
	}


	public void closeSlaTicket(Ticket ticket){

		log.info("Criando historico de mudança automática de status de Ticket por tempo de SLA excedido");

		// Buscar usuário de serviço por id 0
		Users systemUser = userRepository.findById(0L);

		TicketHistory ticketHistory = new TicketHistory();

		ticketHistory.setTicket(ticket);
		ticketHistory.setUser(systemUser);

		String actionString = "";
		switch (ticket.getStatus()){
			case "Pendente":
				actionString = " atualizou o ticket para pendente por tempo do SLA excedido.";
				ticketHistory.setType(HistoryTypes.UPDATE.getDescricao());
				ticketHistory.setDetails("Pendente");
				break;
			case "Sla excedido":
				actionString = " fechou o ticket pois não foi fechado manualmente após SLA excedido";
				ticketHistory.setType(HistoryTypes.SLA.getDescricao());
				ticketHistory.setDetails("Tempo Excedido");
				break;
			default:
				actionString = "Ticket excedido setado com o status incorreto";
		}
        String action = systemUser.getName() + actionString;
		ticketHistory.setAction(action);

		repository.persist(ticketHistory);
	}
}

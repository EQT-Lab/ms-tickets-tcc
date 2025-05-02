package br.com.equatorial.genesys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.equatorial.genesys.model.Group;
import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.TicketAssignment;
import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.repository.TicketAssignmentRepository;
import br.com.equatorial.genesys.request.dto.TicketAssignmentRequestDto;
import br.com.equatorial.genesys.request.dto.TicketRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class TicketAssignmentService {
	
	private final TicketAssignmentRepository repository;
	private final UserService userService;
	private final GroupService groupService;
	private final NotificationsService notificationsService;
	private final TicketHistoryService ticketHistoryService;
	
	@Transactional
	public void create(Ticket ticket, TicketRequestDto ticketRequest) {
		log.info("Atribuindo o ticket {}, para usuarios.", ticket.getDescription());
		
		if(null != ticketRequest.getAssignments()) {
			if(null != ticketRequest.getAssignments().getUsersId()) {
				
				for(Long userId: ticketRequest.getAssignments().getUsersId()) {
					TicketAssignment assignment = new TicketAssignment();
					assignment.setTicket(ticket);
					assignment.setUser(userService.lerUsuario(userId));
					repository.persist(assignment);

					notificationsService.save(assignment.getUser(), ticket.getUser().getName(), ticket.getIndicator().getName(), ticketRequest.getDescription());
					
				}
			}
			
			if(null != ticketRequest.getAssignments().getGroupsId()) {
				for(Long groupId: ticketRequest.getAssignments().getGroupsId()) {
					TicketAssignment assignment = new TicketAssignment();
					assignment.setTicket(ticket);
					assignment.setGroup(groupService.findById(groupId));
					repository.persist(assignment);
				}
			}
			
		}else {
			TicketAssignment assignment = new TicketAssignment();
			assignment.setTicket(ticket);
			assignment.setUser(ticket.getUser());
			repository.persist(assignment);
		}
	}

	@Transactional
	public String update(Ticket ticket, TicketAssignmentRequestDto ticketAssignments, Users user, String descriptionRequest) {
	    log.info("Atribuindo o ticket {}, para usuários.", ticket.getDescription());
	    ticketAssignments.setDescription(descriptionRequest);
	    boolean updated = false;

	    // Listas de IDs atuais no Ticket
	    List<Long> currentUserIds = ticket.getAssignments().stream()
	                                      .map(assignment -> assignment.getUser() != null ? assignment.getUser().getId() : null)
	                                      .filter(Objects::nonNull)
	                                      .toList();

	    List<Long> currentGroupIds = ticket.getAssignments().stream()
	                                       .map(assignment -> assignment.getGroup() != null ? assignment.getGroup().getId() : null)
	                                       .filter(Objects::nonNull)
	                                       .toList();

	    // Listas de IDs novas no ticketAssignments
	    List<Long> newUserIds = ticketAssignments.getUsersId() != null ? ticketAssignments.getUsersId() : List.of();
	    List<Long> newGroupIds = ticketAssignments.getGroupsId() != null ? ticketAssignments.getGroupsId() : List.of();

	    // Encontrar usuários removidos e adicionados
	    List<Long> removedUsers = currentUserIds.stream().filter(id -> !newUserIds.contains(id)).toList();
	    List<Long> addedUsers = newUserIds.stream().filter(id -> !currentUserIds.contains(id)).toList();

	    // Encontrar grupos removidos e adicionados
	    List<Long> removedGroups = currentGroupIds.stream().filter(id -> !newGroupIds.contains(id)).toList();
	    List<Long> addedGroups = newGroupIds.stream().filter(id -> !currentGroupIds.contains(id)).toList();

	    // Deletar todas as atribuições antigas
	    repository.delete("ticket", ticket);

	    // Adicionar novas atribuições de usuários
	    for (Long userId : addedUsers) {
	        TicketAssignment assignment = new TicketAssignment();
	        assignment.setTicket(ticket);
	        assignment.setUser(userService.lerUsuario(userId));
	        repository.persist(assignment);
	        updated = true;
	    }

	    // Adicionar novas atribuições de grupos
	    for (Long groupId : addedGroups) {
	        TicketAssignment assignment = new TicketAssignment();
	        assignment.setTicket(ticket);
	        assignment.setGroup(groupService.findById(groupId));
	        repository.persist(assignment);
	        updated = true;
	    }

	    String historyMessage = "";
	    historyMessage += removedHistoric(ticket, user, descriptionRequest, removedUsers, removedGroups, addedGroups);
	    
	    if (!historyMessage.isEmpty()) {
	    	historyMessage += " e ";
	    }
	    
	    historyMessage += addedHistoric(ticket, user, descriptionRequest, addedUsers, addedGroups);
//	    if (!historyMessage.isEmpty()) {
//	        ticketHistoryService.atribuitionTicket(user, ticket, descriptionRequest, historyMessage);
//	    }
	    
	    return historyMessage;
	}

	private String addedHistoric(Ticket ticket, Users user, String descriptionRequest, List<Long> addedUsers, List<Long> addedGroups) {
	    String addedUsersText = formatUserList(addedUsers);
	    String addedGroupsText = formatGroupList(addedGroups);

	    return createHistoryMessage("Atribuido: ", addedUsersText, addedGroupsText);
	}

	private String removedHistoric(Ticket ticket, Users user, String descriptionRequest, List<Long> removedUsers, List<Long> removedGroups, List<Long> addedGroups) {
	    String removedUsersText = formatUserList(removedUsers);
	    String removedGroupsText = formatGroupList(removedGroups);

	    return createHistoryMessage("Removido: ", removedUsersText, removedGroupsText);
	}

	private String createHistoryMessage(String action, String usersText, String groupsText) {
	    List<String> parts = new ArrayList<>();
	    
	    if (!usersText.isEmpty()) {
	        parts.add(action + usersText);
	    }
	    if (!groupsText.isEmpty()) {
	        parts.add(action + groupsText);
	    }
	    
	    return String.join(". ", parts);
	}

	private String formatUserList(List<Long> userIds) {
	    return userIds.stream()
	        .map(userService::lerUsuario)  // Obtém o objeto usuário
	        .map(Users::getName)           // Obtém o nome do usuário
	        .collect(Collectors.joining(", "));
	}

	private String formatGroupList(List<Long> groupIds) {
	    return groupIds.stream()
	        .map(groupService::findById)  // Obtém o objeto grupo
	        .map(Group::getName)          // Obtém o nome do grupo
	        .collect(Collectors.joining(", "));
	}

	

}

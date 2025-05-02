package br.com.equatorial.genesys.service;

import java.util.List;

import br.com.equatorial.genesys.enums.TicketStatus;
import br.com.equatorial.genesys.model.Comments;
import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.TicketAssignment;
import br.com.equatorial.genesys.model.TicketForwarding;
import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.repository.CommentsRepository;
import br.com.equatorial.genesys.request.dto.CommentsRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class CommentsService {

	private final CommentsRepository repository;
	private final UserService userService;
	private final TicketsService ticketService;
	private final TicketHistoryService ticketHistoryService;
	private final NotificationsService notificationsService;

	@Transactional
	public Comments criarComments(CommentsRequestDto commentsRequestDto) {
		log.info("Criando Comments: {}", commentsRequestDto);
		
		
		Ticket ticket = ticketService.lerTicket(commentsRequestDto.ticketId());
		
		if(TicketStatus.CONCLUIDO.getDescricao().equals(ticket.getStatus()) || TicketStatus.SLA.getDescricao().equals(ticket.getStatus())) {
			log.error("Este ticket não pode ser atualizado porque é " + ticket.getStatus());
			throw new BadRequestException("Este ticket não pode ser atualizado porque é " + ticket.getStatus());
		}
		
		Users user = userService.lerUsuarioByEmail(commentsRequestDto.userEmail());

		
		Comments comments = Comments.builder().ticket(ticket).user(user).comment(commentsRequestDto.description()).build();
		
		repository.persist(comments);
		
		
		ticketHistoryService.createHistoryToComment(ticket, user, commentsRequestDto.description());
		
		String statusNovo = TicketStatus.NOVO.getDescricao();
		if(ticket.getStatus().equals(statusNovo)) {
			ticketService.atualizarStatus(ticket, TicketStatus.ANALISE);
		}
		
		notificationsService.save(ticket.getUser(),user.getName(), ticket.getIndicator().getName() , commentsRequestDto.description());
		
		if(null != ticket.getAssignments()) {
			for(TicketAssignment t : ticket.getAssignments()) {
				if(null != t.getUser()) {
					notificationsService.save(t.getUser(),user.getName(), ticket.getIndicator().getName(), commentsRequestDto.description());
				}
			}
		}
		
		if(null != ticket.getForwardings()) {
			for(TicketForwarding t : ticket.getForwardings()) {
				if(null != t.getUser()) {
					notificationsService.save(t.getUser(),user.getName(), ticket.getIndicator().getName(), commentsRequestDto.description());
				}
			}
		}
		
		return comments;
	}

	public List<Comments> listarCommentsByTicketId(Long ticketId) {
		log.info("Listando Comments por Ticket ID: {}", ticketId);
		Ticket ticket = new Ticket();
		ticket.setId(ticketId);
		
		List<Comments> comments = new CommentsRepository().findByTicket(ticket).list();
		if(comments.isEmpty()) {
			log.error("Não foram encontrados Comments para o Ticket ID: {}", ticketId);
			throw new NotFoundException("Não foram encontrados Comments para o Ticket ID: " + ticketId);
		};
		
		return comments;
	}

	public List<Comments> listarCommentsByUserId(Long userId) {
		log.info("Listando Comments por User ID: {}", userId);
		Users user = new Users();
		user.setId(userId);
		List<Comments> comments = repository.findByUser(user).list();
		
		if(comments.isEmpty()) {
			log.error("Não foram encontrados Comments para o User ID: {}", userId);
			throw new NotFoundException("Não foram encontrados Comments para o User ID: " + userId);
		};
		
		return comments;
	}

}

package br.com.equatorial.genesys.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.equatorial.genesys.config.ConflictRequestException;
import br.com.equatorial.genesys.enums.TicketStatus;
import br.com.equatorial.genesys.exception.model.TicketsNotFoundException;
import br.com.equatorial.genesys.model.Indicator;
import br.com.equatorial.genesys.model.IndicatorTicket;
import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.TicketAssignment;
import br.com.equatorial.genesys.model.TicketForwarding;
import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.repository.TicketRepository;
import br.com.equatorial.genesys.request.dto.IndicatorRequestDto;
import br.com.equatorial.genesys.request.dto.Pagination;
import br.com.equatorial.genesys.request.dto.TicketCloseRequestDto;
import br.com.equatorial.genesys.request.dto.TicketPaginatedResponse;
import br.com.equatorial.genesys.request.dto.TicketRequestDto;
import br.com.equatorial.genesys.request.dto.TicketSlaRequestDto;
import br.com.equatorial.genesys.request.dto.TicketUpdateRequestDto;
import br.com.equatorial.genesys.util.StatusUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class TicketsService {

	private final TicketRepository repository;

	private final UserService userService;
	
	private final IndicatorService indicatorService;
	
	private final RegionalService regionalService;
	private final DistributorService distributorService;
	
	private final TicketAssignmentService ticketAssignmentService;
	private final TicketForwardingService ticketForwardingService;
	
	private final IndicatorTicketService indicatorTicketService;
	
	private final TicketHistoryService ticketHistoryService;
	
	private final EmailService emailService;
	private final OAuthService authService;

	private final NotificationsService notificationsService;
	
	private final ObjectMapper objectMapper;

	@Transactional
	public Ticket criarTicket(TicketRequestDto ticketRequest) {
		log.info("Criando Ticket: {}", ticketRequest);

			Ticket ticket = new Ticket();
			ticket.setStatus(TicketStatus.NOVO.getDescricao());
			ticket.setUser(userService.identificarUser(ticketRequest.getUser(), ticketRequest.getEmail()));
			
			if(null != ticketRequest.getRegional()) {
				ticket.setRegional(regionalService.lerRegional(ticketRequest.getRegional()));
			}
			
			if(null!= ticketRequest.getDistributor()) {
				ticket.setDistributor(distributorService.lerDistributor(ticketRequest.getDistributor()));
			}
			
			ticket.setIndicator(indicatorService.findByIndicatorIdAndModuleId(ticketRequest.getIndicator(),
					ticketRequest.getModuleId()));

			ticket.setDescription(ticketRequest.getDescription());
			ticket.setSla(ticket.getIndicator().getSla());
			
			if(null != ticketRequest.getExternalId()) {
				ticket.setExternalId(ticketRequest.getExternalId());
			}
			
			if (null != ticketRequest.getAtendimentoPresencialModalData()) {
		        JsonNode atendimentoData = objectMapper.valueToTree(ticketRequest.getAtendimentoPresencialModalData());
		        ticket.setAtendimentoPresencialModalData(atendimentoData);
			}
			
			
			Optional<Ticket> ticketExistis = repository.findFirstTicketIdWithSameAttributes(ticket);
			if (ticketExistis.isPresent()) {
				log.error("Já existe ticket para esse indicator, regional e distribuidora com ID {}", ticketExistis.get().getId());
				throw new ConflictRequestException("Já existe um ticket aberto para este indicador na mesma localidade. É necessário fechar o ticket atual antes de abrir um novo. Procure o Ticket #" + ticketExistis.get().getId() + ".");
			}

			repository.persist(ticket);

			if (null != ticketRequest.getIndicatorDataSummary() && !ticketRequest.getIndicatorDataSummary().isEmpty()) {
				indicatorSet(ticketRequest.getIndicatorDataSummary(), ticket);
			}

			ticketAssignmentService.create(ticket, ticketRequest);

			ticketForwardingService.create(ticket, ticketRequest);

			ticketHistoryService.create(ticket);

			//External Notification
			String accessToken = null;
			try {

				accessToken = authService.getAccessToken();
				String subject = "Novo Ticket Delfos criado!";
				Set<String> targetMail = emailService.getTicketUsersMail(ticket);

				emailService.sendSlaMailGraph(accessToken, targetMail, subject, ticket, "newTicketMail");

			} catch (IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}

			return ticket;

	}
	
	private void indicatorSet(List<IndicatorRequestDto> indicators, Ticket ticket) {
		try {
		for(IndicatorRequestDto indicatorDto : indicators) {
			log.info("Atribuindo Indicador {} ao ticket", indicatorDto.getName());
			
			IndicatorTicket indicatorTicket = new IndicatorTicket();
			indicatorTicket.setTicket(ticket);
			indicatorTicket.setName(indicatorDto.getName());
			indicatorTicket.setValue(indicatorDto.getValue());
			indicatorTicket.setColor(indicatorDto.getStatus_color());

			indicatorTicketService.create(indicatorTicket);
		}

		}catch(Exception e) {
			log.error("Erro ao atribuir Indicador ao Ticket", e);
			throw new BadRequestException("Erro ao atribuir Indicador ao Ticket");
		}
	}

	public Ticket lerTicket(Long id) {
		log.info("Buscando Ticket pelo ID: {}", id);
		
		Ticket ticket = repository.findById(id);
		if(null == ticket) {
			log.error("Ticket por ID {} nao encontrado", id);
			throw new TicketsNotFoundException(String.format("Ticket por ID "+ id +" não encontrado"));
		}
		
		ticket.setIndicatorTickets(indicatorTicketService.findByTicketId(ticket.getId()));
		return ticket;
	}

	public List<Ticket> listarTickets(String dtCreationStart, String dtCreationEnd, String status, Long module, String assigned,
									  String forwarded, String createdby, String ticketString, String xUserId, String dtClosedStart, String dtClosedEnd) {
		 
		log.info("Listando tickets com filtros: dtCreationStart {}, dtCreationEnd {}, status {}, module {}, assigned {}, forwarded {}, createdBy {}, myTickets {}, xUserId {}, dtClosedStart {}, dtClosedEnd {}",
				dtCreationStart, dtCreationEnd, status, module, assigned, forwarded, createdby, ticketString, xUserId, dtClosedStart, dtClosedEnd);

		LocalDateTime startCreationDateTime = null;
	    LocalDateTime endCreationDateTime = null;
	    
	    LocalDateTime startClosedDateTime = null;
	    LocalDateTime endClosedDateTime = null;

	    if (dtCreationStart != null && !dtCreationStart.isEmpty()) {
	        try {
	            startCreationDateTime = LocalDateTime.parse(dtCreationStart);
	        } catch (DateTimeParseException e) {
	            log.error("Formato inválido para dtStart: {}", dtCreationStart, e);
	            throw new BadRequestException("Formato inválido para dtCreationStart. Esperado: yyyy-MM-ddTHH:mm:ss");
	        }
	    }

	    if (dtCreationEnd != null && !dtCreationEnd.isEmpty()) {
	        try {
	            endCreationDateTime = LocalDateTime.parse(dtCreationEnd);
	        } catch (DateTimeParseException e) {
	            log.error("Formato inválido para dtEnd: {}", dtCreationEnd, e);
	            throw new BadRequestException("Formato inválido para dtCreationEnd. Esperado: yyyy-MM-ddTHH:mm:ss");
	        }
	    }
	    
	    if (dtClosedStart != null && !dtClosedStart.isEmpty()) {
	        try {
	        	startClosedDateTime = LocalDateTime.parse(dtClosedStart);
	        } catch (DateTimeParseException e) {
	            log.error("Formato inválido para dtStart: {}", dtClosedStart, e);
	            throw new BadRequestException("Formato inválido para dtClosedStart. Esperado: yyyy-MM-ddTHH:mm:ss");
	        }
	    }

	    if (dtClosedEnd != null && !dtClosedEnd.isEmpty()) {
	        try {
	        	endClosedDateTime = LocalDateTime.parse(dtClosedEnd);
	        } catch (DateTimeParseException e) {
	            log.error("Formato inválido para dtEnd: {}", dtClosedEnd, e);
	            throw new BadRequestException("Formato inválido para dtClosedEnd. Esperado: yyyy-MM-ddTHH:mm:ss");
	        }
	    }


		boolean myTickets = false;

		if (ticketString != null) {
			if (ticketString.equalsIgnoreCase("true") || ticketString.equalsIgnoreCase("false")) {
				myTickets = Boolean.parseBoolean(ticketString);
			} else {
				throw new BadRequestException("Formato inválido para myTickets. Esperado: true ou false");
			}
		}

		Users user = userService.lerUsuarioByEmail(xUserId);
		
		log.info("Aplicando filtros e realizando consulta");
		List<Ticket> list = repository.findWithFilters(startCreationDateTime, endCreationDateTime, status, module, assigned, forwarded,
				createdby, myTickets, user, startClosedDateTime, endClosedDateTime).list();

		log.info("Total de tickets encontrado {}", list.size());

		
		list.sort(Comparator.comparingInt((Ticket ticket) -> StatusUtil.statusPriority
				.getOrDefault(ticket.getStatus(), Integer.MIN_VALUE))
				 .thenComparing(Ticket::getCreated, Comparator.reverseOrder())); 
		
		return list;
	}

	public List<Ticket> listartTicketsPorIndicador(Indicator indicator) {
		log.info("Listando Tickets por Indicador: {}", indicator);
		
		if(indicator.getId() == null) {
			if(indicator.getName() != null) {
				indicator = indicatorService.lerIndicatorByName(indicator.getName());
			}else {
				log.info("Não foi localizado Tickets pelo indicador informado {}", indicator);
				throw new TicketsNotFoundException(String.format("Não foi localizado Tickets pelo indicador informado {}", indicator));
			}
		}
		
		PanacheQuery<Ticket> byIndicator = repository.findByIndicator(indicator);
		
		if(byIndicator.count() == 0) {
			throw new TicketsNotFoundException(String.format("Não foi localizado Tickets pelo indicador informado {}", indicator));
		}
		
		return byIndicator.list();
	}
	
	public List<Ticket> listartTicketsPorUsers(Users user) {
		
		log.info("Listando Tickets por User: {}", user);

		if(user.getId() == null) {
			if(user.getName() != null) {
				user = userService.lerUsuarioByName(user.getName());
			}else if(user.getEmail() != null) {
				user = userService.lerUsuarioByEmail(user.getEmail());
//			}else if(user.getUserAd() != null) {
//				user = userService.lerUsuarioByUserAd(user.getUserAd());
			}else {
				log.error("Não foi localizado Tickets pelo usuário informado {}", user);
				throw new TicketsNotFoundException(String.format("Não foi localizado Tickets pelo usuário informado {}", user));
			}
		}
		
		PanacheQuery<Ticket> byUsers = repository.findByUsers(user);
		
		if(byUsers.count() == 0) {
			throw new TicketsNotFoundException(String.format("Não foi localizado Tickets pelo usuário informado {}", user));
		}
		
		return byUsers.list();
	}

	@Transactional
	public void atualizarEncaminhamento(Long id, TicketUpdateRequestDto ticket, String xUserId) {
		log.info("Encaminhando Ticket: {}", ticket);
		Ticket ticketToUpdate = lerTicket(id);
		
		if(TicketStatus.CONCLUIDO.getDescricao().equals(ticketToUpdate.getStatus()) || TicketStatus.SLA.getDescricao().equals(ticketToUpdate.getStatus())) {
			log.error("Este ticket não pode ser atualizado porque é " + ticketToUpdate.getStatus());
			throw new BadRequestException("Este ticket não pode ser atualizado porque é " + ticketToUpdate.getStatus());
		}
		
		Users user = userService.lerUsuarioByEmail(xUserId);
		
		if(!userCandUpdateThisTicket(user, ticketToUpdate)) {
			log.error("O usuário {} não pode atualizar este ticket", user.getName());
			throw new BadRequestException("O usuário "+ user.getName() +" não pode atualizar este ticket");
		}
		
		if(null != ticket.getForwardings()) {
			if(ticketForwardingService.addeUser(ticketToUpdate, ticket.getForwardings())) {
				ticket.getForwardings().setDescription(ticket.getDescription());
				ticketHistoryService.forwardingTicket(user, ticketToUpdate, ticket.getForwardings());
			}
		}

		//External Notification
		String accessToken = null;
		try {

			accessToken = authService.getAccessToken();
			String subject = "Ticket Delfos encaminhado a você!";

			List<Long> usersId = ticket.getForwardings().getUsersId();
			List<String> newForwardsUsers = new ArrayList<>();

			for (Long userId: usersId){
				newForwardsUsers.add(userService.lerUsuario(userId).getEmail());
			}

			Set<String> targetMail = new HashSet<>(newForwardsUsers);

			emailService.sendSlaMailGraph(accessToken, targetMail, subject, ticketToUpdate, "newForwardMail");

		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Transactional
	public void atualizarAtribuicao(Long id, TicketUpdateRequestDto ticketRequest, String xUserId) {
		log.info("Atualizando Ticket: {}", ticketRequest);
		Ticket ticketToUpdate = lerTicket(id);
		
		if(TicketStatus.CONCLUIDO.getDescricao().equals(ticketToUpdate.getStatus()) || TicketStatus.SLA.getDescricao().equals(ticketToUpdate.getStatus())) {
			log.error("Este ticket não pode ser atualizado porque é " + ticketToUpdate.getStatus());
			throw new BadRequestException("Este ticket não pode ser atualizado porque é " + ticketToUpdate.getStatus());
		}
		
		Users user = userService.lerUsuarioByEmail(xUserId);
		
		if(!userCandUpdateThisTicket(user, ticketToUpdate)) {
			log.error("O usuário {} não pode atualizar este ticket", user.getName());
			throw new BadRequestException("O usuário "+ user.getName() +" não pode atualizar este ticket");
		}
		
		String historicMessage = "";
		if(null != ticketRequest.getAssignments()) {
			historicMessage = ticketAssignmentService.update(ticketToUpdate, ticketRequest.getAssignments(), user, ticketRequest.getDescription());
		}
		
		if(null != ticketRequest.getForwardings()) {
			ticketRequest.getForwardings().setDescription(ticketRequest.getDescription());
			historicMessage += ticketForwardingService.forwardingText(user, ticketToUpdate, ticketRequest.getForwardings());
		}
		
		ticketHistoryService.atribuitionTicket(user, ticketToUpdate, ticketRequest.getDescription(), historicMessage);
		
	}
	
	private boolean userCandUpdateThisTicket(Users user, Ticket ticketToUpdate) {
		log.info("Validando se o usuário {} pode atualizar o ticket", user.getName());
		
		if(user.equals(ticketToUpdate.getUser())) {
			return true;
		}
		
		for(TicketAssignment assignment : ticketToUpdate.getAssignments()) {
			if(user.equals(assignment.getUser())) {
				return true;
			}			
		}
		
		for(TicketForwarding forwarding : ticketToUpdate.getForwardings()) {
			if(user.equals(forwarding.getUser())) {
				return true;
			}			
		}
		
		return false;
		
	}

	@Transactional
	public void fecharTicket(Long id, TicketCloseRequestDto ticket) {
		log.info("Fechando Ticket: {}", id);
		Ticket ticketToUpdate = lerTicket(id);

		if(ticketToUpdate.getStatus().equals(TicketStatus.CONCLUIDO.getDescricao())) {
			log.info("O Ticket já está fechado");
			throw new BadRequestException("O Ticket já está fechado");
		}
		Users user = userService.lerUsuarioByEmail(ticket.getUserEmail());

		if(!userCandUpdateThisTicket(user, ticketToUpdate)) {
			log.error("O usuário {} não pode fechar este ticket", user.getName());
			throw new BadRequestException("O usuário "+ user.getName() +" não pode fechar este ticket");
		}

		ticketToUpdate.setStatus(TicketStatus.CONCLUIDO.getDescricao());
		
		if (Objects.equals(ticketToUpdate.getStatus(), TicketStatus.PENDENTE.getDescricao())) {
			ticketToUpdate.setStatus((TicketStatus.SLA.getDescricao()));
		}
		
		ticketToUpdate.setClosed(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime());
		ticketHistoryService.closeTicket(ticketToUpdate, user);

		
		sendNotificationsToAssignmentsAndForwardings(ticketToUpdate, ticketToUpdate.getDescription());

		//External Notification
		String accessToken = null;
		try {

			accessToken = authService.getAccessToken();
			String subject = "Ticket Delfos concluído!";
			Set<String> targetMail = emailService.getTicketUsersMail(ticketToUpdate);

			emailService.sendSlaMailGraph(accessToken, targetMail, subject, ticketToUpdate, "newTicketMail");

		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	private void sendNotificationsToAssignmentsAndForwardings(Ticket ticket, final String text) {
		if(null != ticket.getAssignments()) {
			for(TicketAssignment t : ticket.getAssignments()) {
				if(null != t.getUser()) {
					notificationsService.save(t.getUser(), ticket.getUser().getName(), ticket.getIndicator().getName(), text);
				}
			}
		}
		
		if(null != ticket.getForwardings()) {
			for(TicketForwarding t : ticket.getForwardings()) {
				if(null != t.getUser()) {
					notificationsService.save(t.getUser(), ticket.getUser().getName(), ticket.getIndicator().getName(), text);
				}
			}
		}
	}

	@Transactional
	public List<TicketSlaRequestDto> atualizarSla() {

		log.info("Iniciando rotina de checagem de sla");

		String status = TicketStatus.NOVO.getDescricao();

		List<Ticket> ticketList = repository.findWithFilters(status).list();
		List<TicketSlaRequestDto> updatedTickets = new ArrayList<>();


        String accessToken = null;
        try {
            accessToken = authService.getAccessToken();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

		for (Ticket ticket : ticketList) {

			Long minutesSinceCreation = Duration.between(LocalDateTime.now(), ticket.getCreated()).toMinutes();
			Set<String> emailSet = emailService.getTicketUsersMail(ticket);

			if (ticket.getSla() < minutesSinceCreation && Objects.equals(ticket.getStatus(), status)) {

				String subject = "Ticket Delfos Excedido! - " + ticket.getIndicator().getName();

                try {
                    emailService.sendSlaMailGraph(accessToken, emailSet, subject, ticket,
                            "slaMail");
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Atualizar DTO
				TicketSlaRequestDto dto = new TicketSlaRequestDto();
				dto.setId(ticket.getId());
				dto.setStatus(ticket.getStatus());
				dto.setIndicatorName(ticket.getIndicator().getName());
				dto.setEmails(emailSet);

				updatedTickets.add(dto);

				// Mudar status para Pendente
				ticket.setStatus(TicketStatus.PENDENTE.getDescricao());

				notificationsService.save(ticket.getUser(), ticket.getUser().getName(), ticket.getIndicator().getName(), ticket.getDescription());
				ticketHistoryService.closeSlaTicket(ticket);
			}
		}

		return updatedTickets;
	}

	@Transactional
	public void fecharSla() {

		log.info("Iniciando rotina de encerramento de tickets pendentes");

		String status = TicketStatus.PENDENTE.getDescricao();

		List<Ticket> ticketList = repository.findWithFilters(status).list();

		for (Ticket ticket : ticketList) {
			long hoursSinceCreation = Duration.between(ticket.getCreated(), LocalDateTime.now()).toHours();

			if (ticket.getSla() < hoursSinceCreation + 24) {

				// Fecha o ticket
				ticket.setStatus(TicketStatus.SLA.getDescricao());
				ticket.setClosed(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime());
				ticketHistoryService.closeSlaTicket(ticket);

				notificationsService.save(ticket.getUser(), ticket.getUser().getName(), ticket.getIndicator().getName(), ticket.getDescription());
				
				sendNotificationsToAssignmentsAndForwardings(ticket, ticket.getDescription());

			}
		}
	}

	public boolean atualizarStatus(Ticket ticket, TicketStatus status) {

		if(null != ticket) {
			log.info("Atualizando o status do Ticket ID {} para {}", ticket.getId(), status.getDescricao());
			Ticket ticketUpdate = repository.findById(ticket.getId());
			ticketUpdate.setStatus(status.getDescricao());
			repository.persist(ticketUpdate);
			return true;
		}

		return false;
	}

	public List<String> listAllByExternalId(List<String> externalId) {
		log.info("Busca de tickets por externalId {}", externalId);		
		return repository.findExternalIds(externalId);
	}

	public TicketPaginatedResponse buscaPaginada(int page, int size) {
		log.info("Busca de tickets paginada -> pagina: {}; tamanho: {}", page, size);

		int currentPage = Math.max(page, 1);
		int pageSize = Math.max(size, 1);

		List<Ticket> tickets = repository.findAllPagination(currentPage - 1, pageSize);
		long totalItems = repository.count();
		int totalPages = (int) Math.ceil((double) totalItems / pageSize);

		log.info("Encontrado {} tickets", tickets.size());
		tickets.forEach(ticket -> {
			ticket.setIndicatorTickets(indicatorTicketService.findByTicketId(ticket.getId()));
		});

		return new TicketPaginatedResponse(tickets,
				new Pagination(currentPage,
						(currentPage < totalPages) ? currentPage + 1 : null,
						(currentPage > 1) ? currentPage - 1 : null,
						totalPages,
						1,
						pageSize,
						totalPages));
	}

}

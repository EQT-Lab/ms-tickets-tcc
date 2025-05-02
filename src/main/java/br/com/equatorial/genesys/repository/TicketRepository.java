package br.com.equatorial.genesys.repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import br.com.equatorial.genesys.model.Indicator;
import br.com.equatorial.genesys.model.Regional;
import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.Users;
import br.com.equatorial.genesys.util.StatusUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class TicketRepository implements PanacheRepository<Ticket> {
	
	public PanacheQuery<Ticket> findWithFilters(LocalDateTime dtCreationStart, LocalDateTime dtCreationEnd, String status, Long module,
												String assigned, String forwarded, String createdby, boolean myTickets,
												Users xUserId, LocalDateTime startClosedDateTime, LocalDateTime endClosedDateTime) {
		String query = "";
		Map<String, Object> params = new HashMap<>();

		if (dtCreationStart != null) {
			query += " created >= :dtCreationStart ";
			params.put("dtCreationStart", dtCreationStart);
		}

		if (dtCreationEnd != null) {
			if (!query.isEmpty()) {
				query += " and ";
			}
			query += " created <= :dtCreationEnd ";
			params.put("dtCreationEnd", dtCreationEnd);
		}

		if (status != null){
			if (!query.isEmpty()){
				query += " and ";
			}
			 query += " unaccent(lower(status)) = unaccent(lower(:status)) ";
			params.put("status", status);
		}

		if (module != null){
			if (!query.isEmpty()){
				query += " and ";
			}
			query += "indicator.module.id = :module";

			params.put("module", module);
		}

		if (assigned != null){
			if (!query.isEmpty()){
				query += " and ";
			}
			assigned = '%' + assigned + '%';
			query += " element(assignments).user IS NOT NULL AND lower(element(assignments).user.name) like lower(:assigned) ";

			params.put("assigned", assigned);
		}

		if (forwarded != null){
			if (!query.isEmpty()){
				query += " and ";
			}
			forwarded = '%' + forwarded + '%';
			query += " element(forwardings).user IS NOT NULL AND lower(element(forwardings).user.name) like lower(:forwarded) ";

			params.put("forwarded", forwarded);
		}

		if (createdby != null){
			if (!query.isEmpty()){
				query += " and ";
			}
			createdby = '%' + createdby + '%';
			query += " lower(user.name) like lower(:createdby) ";

			params.put("createdby", createdby);
		}

		if (myTickets){
			if (!query.isEmpty()){
				query += " and ";
			}
			query += " user = :xuser " +
					" or element(forwardings).user = :xuser" +
					" or element(assignments).user = :xuser";

			params.put("xuser", xUserId);
		}
		
		
		if (startClosedDateTime != null) {
			if (!query.isEmpty()){
				query += " and ";
			}
			query += " closed >= :dtClosedStart ";
			params.put("dtClosedStart", startClosedDateTime);
		}

		if (endClosedDateTime != null) {
			if (!query.isEmpty()) {
				query += " and ";
			}
			query += " closed <= :dtClosedEnd ";
			params.put("dtClosedEnd", endClosedDateTime);
		}
		
		log.info("Executando a query: {}", query);
		return find(query, params);
	}

	// Função Sobrecarregada para filtragem apenas por filtros
	public PanacheQuery<Ticket> findWithFilters(String status) {
		return findWithFilters(null, null, status, null, null, null, null, false, null, null, null);
	}
	
	public Optional<Ticket> findFirstTicketIdWithSameAttributes(Ticket ticket) {
	    String query = " FROM Ticket WHERE indicator = :indicator " +
	                   " AND status IN (:status) ";

	    List<String> statuses = List.of("Novo", "Análise");
	    Parameters parameters = Parameters.with("indicator", ticket.getIndicator())
        .and("status", statuses);
	    
	    if(null != ticket.getDistributor()) {
	    	parameters.and("distributor", ticket.getDistributor());
	    	query += " AND distributor = :distributor ";
	    }
	    
	    if(null != ticket.getRegional()) {
	    	parameters.and("regional", ticket.getRegional());
	    	query += " AND regional = :regional ";
	    }

	    return find(query, parameters).firstResultOptional();
	    
	}

	public boolean existsTicketWithSameAttributes(Ticket ticket) {
	        String query = " indicator = :indicator AND regional = :regional  " +
	                       " AND status IN (:status) ";

	        List<String> statuses = List.of("Novo", "Análise");

	        long count = count(query,
	                Parameters.with("indicator", ticket.getIndicator())
	                          .and("regional", ticket.getRegional())
	                          .and("status", statuses));
	        
	        return count > 0;
	    }


	public PanacheQuery<Ticket> findBySla(String sla) {
		return find("sla", sla);
	}

	public PanacheQuery<Ticket> findByRegional(Regional regional){
		return find("regional", regional);
	}

	public PanacheQuery<Ticket> findByUsers(Users user){
		return find("user", user);
	}

	public PanacheQuery<Ticket> findByIndicator(Indicator indicator){
		return find("indicator", indicator);
	}
	
	  public List<String> findExternalIds(List<String> externalIds) {

		  String idListString = externalIds.stream().map(id -> "'" + id + "'").collect(Collectors.joining(", "));

		  String query = "SELECT t.externalId FROM Ticket t WHERE t.externalId IN (" + idListString + ")";

		  List<String> result = find(query).project(String.class).list();
		  	log.info("Result: {}", result.toString());

		  return result;
	    }

		public List<Ticket> findAllPagination(int page, int size) {
			List<Ticket> tickets = findAll().list();

			page = Math.max(page, 1);
			
			tickets.sort(Comparator.comparingInt(
					(Ticket ticket) -> StatusUtil.statusPriority.getOrDefault(ticket.getStatus(), Integer.MIN_VALUE))
					.thenComparing(Ticket::getCreated, Comparator.reverseOrder()));

			return tickets.stream()
					.skip((long) (page - 1) * size)
					.limit(size)
					.collect(Collectors.toList());
		}

}

package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Comments;
import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.Users;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;


@Singleton
public class CommentsRepository implements PanacheRepository<Comments> {
	
	public PanacheQuery<Comments> findByTicket(Ticket ticket){
		return find("ticket", ticket);
	}
	
	public PanacheQuery<Comments> findByUser(Users user){
		return find("user", user);
	}

}

package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Notifications;
import br.com.equatorial.genesys.model.Users;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class NotificationsRepository  implements PanacheRepository<Notifications> {

	public PanacheQuery<Notifications> findByUser(Users user){
		return find("user", user);
	}

	public PanacheQuery<Notifications>  findByUserEmail(String userEmail) {
		return find("user.email", userEmail);
	}

	@Transactional
	public void setAllReadByEmail(String userEmail) {
		update("active = false, updated =  CAST(now() AS TIMESTAMP) WHERE user.email = ?1", userEmail);
	}
	
	@Transactional
	public PanacheQuery<Notifications> findByIdAndEmail(Long id, String userEmail) {
		return find("id = ?1 AND user.email = ?2", id, userEmail);
	}
	
	@Transactional
	public void deleteAllByEmail(String userEmail) {
		delete("user.email = ?1", userEmail);
	}
	
	@Transactional
	public void deleteAllById(Long id, String userEmail) {
		delete("id = ?1 AND user.email = ?2", id, userEmail);
	}
}

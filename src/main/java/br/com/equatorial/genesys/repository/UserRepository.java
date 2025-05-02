package br.com.equatorial.genesys.repository;

import br.com.equatorial.genesys.model.Users;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;


@Singleton
public class UserRepository implements PanacheRepository<Users> {

	public Users findByEmail(String email) {
		return find("email", email).firstResult();
	}
	
	public Users findByName(String name) {
		return find("name", name).firstResult();
	}

	public Users findById(Long id){
		return find("id", id).firstResult();
	}
}


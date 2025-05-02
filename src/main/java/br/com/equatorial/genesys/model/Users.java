package br.com.equatorial.genesys.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_USERS")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "NAME")
	private String name;
	
    private String email;
    
    private LocalDateTime created;
    private LocalDateTime updated;
    
    @PrePersist
    public void prePersist() {
    	this.created = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Users other = (Users) obj;
		return Objects.equals(email.toLowerCase(), other.email.toLowerCase()) && Objects.equals(name.toLowerCase(), other.name.toLowerCase());
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, name);
	}
    
    

}

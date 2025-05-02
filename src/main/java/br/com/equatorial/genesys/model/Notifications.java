package br.com.equatorial.genesys.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "TB_NOTIFICATIONS")
public class Notifications {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
	
	private String ticketName;
	
	private String description;
	
	private String createdBy;
	
	private boolean active;
	
	private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
    	this.active = true;
    	this.created = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
    }
    
    @PreUpdate
	public void preUpdate() {
		this.updated = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
	}

}

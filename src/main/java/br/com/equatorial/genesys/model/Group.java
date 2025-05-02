package br.com.equatorial.genesys.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "tb_group")
@NoArgsConstructor
@AllArgsConstructor
public class Group {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 512)
	private String name;
	
	@ManyToMany
	@JoinTable(
		name = "tb_group_distributor",
		joinColumns = @JoinColumn(name = "group_id"),
		inverseJoinColumns = @JoinColumn(name = "distributor_id")
	)
	private List<Distributor> distributors;
	
	@ManyToMany
	@JoinTable(
		name = "tb_group_regional",
		joinColumns = @JoinColumn(name = "group_id"),
		inverseJoinColumns = @JoinColumn(name = "regional_id")
	)
	private List<Regional> regionais;
	
	@ManyToMany
	@JoinTable(
		name = "tb_group_user_assignment",
		joinColumns = @JoinColumn(name = "group_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private List<Users> usersAssignment;
	
	
	@ManyToMany
	@JoinTable(
		name = "tb_group_user_forwarded",
		joinColumns = @JoinColumn(name = "group_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private List<Users> usersForwarded;
	
	
	@ManyToMany
	@JoinTable(
		name = "tb_group_modulo",
		joinColumns = @JoinColumn(name = "group_id"),
		inverseJoinColumns = @JoinColumn(name = "modulo_id")
	)
	private List<Modulo> modulos;
	
	private boolean active;
	
	private LocalDateTime created;
	
	private LocalDateTime updated;

}

package br.com.equatorial.genesys.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "TB_TICKET")
public class Ticket {
	   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "indicator_id")
    private Indicator indicator;
    
    @ManyToOne
    @JoinColumn(name = "distributor_id")
    private Distributor distributor;
    
    @ManyToOne
    @JoinColumn(name = "regional_id")
    private Regional regional;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode atendimentoPresencialModalData;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketAssignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TicketForwarding> forwardings = new ArrayList<>();
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketHistory> history = new ArrayList<>();
    
    
    @Transient
    private List<IndicatorTicket> indicatorTickets = new ArrayList<>();

    private String description;
    
    @Column(nullable = false)
    private String status;
    
    private Long sla;
    
    private String externalId;
	
    private LocalDateTime created;
    private LocalDateTime updated;
    private LocalDateTime closed;

    @PrePersist
    public void prePersist() {
    	this.created = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
    }
    
    @PreUpdate
	public void preUpdate() {
		this.updated = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
	}
    
}

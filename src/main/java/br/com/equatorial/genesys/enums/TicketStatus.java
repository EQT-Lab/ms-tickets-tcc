package br.com.equatorial.genesys.enums;

import jakarta.ws.rs.BadRequestException;
import lombok.Getter;

@Getter
public enum TicketStatus {

	NOVO("Novo"), ANALISE("Análise"), CONCLUIDO("Concluído"), PENDENTE("Pendente"), SLA("Tempo excedido");

	private final String descricao;

	TicketStatus(String descricao) {
		this.descricao = descricao;
	}

    public static String getDescricaoByName(String name) {
        for (TicketStatus status : TicketStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status.getDescricao();
            }
        }
        throw new BadRequestException("Status inválido: " + name);
    }
}

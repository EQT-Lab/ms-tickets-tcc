package br.com.equatorial.genesys.enums;

import jakarta.ws.rs.BadRequestException;

public enum HistoryTypes {
	
	CREATION("creation"),
	UPDATE("update"),
	FORWARDING("forwarding"),
	CLOSED("close"),
	COMMENT("comment"),
	SLA("sla");
	
	private final String descricao;

	HistoryTypes(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static String getDescricaoByName(String name) {
        for (HistoryTypes status : HistoryTypes.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status.getDescricao();
            }
        }
        throw new BadRequestException("Type inválido: " + name);
    }


}

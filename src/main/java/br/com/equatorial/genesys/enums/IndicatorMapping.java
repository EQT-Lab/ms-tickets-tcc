package br.com.equatorial.genesys.enums;

import jakarta.ws.rs.BadRequestException;

public enum IndicatorMapping {

    // Call Center
    FILA_INS_CLIENTE(1, 1, "Fila INS Cliente"),
    TME(1, 2, "TME"),

 // Reputação
    SAUDE_DA_MARCA(2, 1, "Redes Sociais | Menções Negativas"),
    FAVORABILIDADE(2, 2, "Imprensa | Favorabilidade"),

    // Agências
    INS_ATENDIMENTO(3, 1, "INS Atendimento"),
    FUNCIONAMENTO(3, 2, "Funcionamento"),

    // COM
    CLIENTES_AFETADOS(4, 1, "Clientes Afetados"),

    // IA
    COMPORTAMENTO_INADEQUADO(5, 1, "Comportamento Inadequado"),
    RISCO_A_VIDA(5, 2, "Risco a Vida"),
    RISCO_A_IMAGEM(5, 3, "Risco a Imagem");

    private final int moduleId;
    private final int indicatorId;
    private final String name;

    IndicatorMapping(int moduleId, int indicatorId, String name) {
        this.moduleId = moduleId;
        this.indicatorId = indicatorId;
        this.name = name;
    }

    public int getModuleId() {
        return moduleId;
    }

    public int getIndicatorId() {
        return indicatorId;
    }

    public String getName() {
        return name;
    }

    /**
     * Retorna o nome do indicador com base no moduleId e no indicatorId.
     *
     * @param moduleId    O ID do módulo.
     * @param indicatorId O ID do indicador.
     * @return O nome correspondente.
     * @throws IllegalArgumentException Se não encontrar correspondência.
     */
    public static String getNameByIds(int moduleId, int indicatorId) {
        for (IndicatorMapping mapping : values()) {
            if (mapping.moduleId == moduleId && mapping.indicatorId == indicatorId) {
                return mapping.name;
            }
        }
        throw new BadRequestException(
            "Nenhum mapeamento encontrado para moduleId: " + moduleId + ", indicatorId: " + indicatorId
        );
    }
    
    public static String getName(int moduleId, String indicatorName) {
    	for (IndicatorMapping mapping : values()) {
			if (mapping.moduleId == moduleId && mapping.name.equalsIgnoreCase(indicatorName)) {
				return mapping.name;
			}
		}
		throw new BadRequestException(
			"Nenhum mapeamento encontrado para moduleId: " + moduleId + ", indicatorName: " + indicatorName
		);
    }
}



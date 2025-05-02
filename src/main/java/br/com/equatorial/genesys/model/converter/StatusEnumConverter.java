package br.com.equatorial.genesys.model.converter;

import br.com.equatorial.genesys.enums.TicketStatus;
import jakarta.inject.Singleton;
import jakarta.persistence.AttributeConverter;

@Singleton
public class StatusEnumConverter implements AttributeConverter<TicketStatus, String> {

    @Override
    public String convertToDatabaseColumn(TicketStatus status) {
        return status == null ? null : status.name(); // Converte para String
    }

    @Override
    public TicketStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TicketStatus.valueOf(dbData); // Converte de volta para enum
    }

}

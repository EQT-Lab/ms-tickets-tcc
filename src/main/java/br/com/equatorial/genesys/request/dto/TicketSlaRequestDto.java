package br.com.equatorial.genesys.request.dto;

import lombok.Data;

import java.util.Set;

@Data
public class TicketSlaRequestDto {
    private Long id;
    private String status;
    private String indicatorName;
    private Set<String> emails;
}

package br.com.equatorial.genesys.request.dto;

import lombok.Data;

@Data
public class AgenciaRequestDto {

    private Long id;
    private String name;
    private String type;
    private Long regionalId;

}

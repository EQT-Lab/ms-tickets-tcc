package br.com.equatorial.genesys.request.dto;

import lombok.Data;

@Data
public class DirectMailRequestDto {

    private String targetMail;
    private String message;
    private String subject;
    private String id;
}

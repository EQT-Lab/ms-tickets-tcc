package br.com.equatorial.genesys.request.dto;

public record CommentsRequestDto(Long ticketId, String userEmail, String description) {

}

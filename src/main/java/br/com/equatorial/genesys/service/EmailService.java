package br.com.equatorial.genesys.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.equatorial.genesys.model.Ticket;
import br.com.equatorial.genesys.model.TicketAssignment;
import br.com.equatorial.genesys.model.TicketForwarding;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EmailService {

    @ConfigProperty(name = "quarkus.mailer.from")
    private String sourceMail;

    private final OAuthService authService;
    private final TicketsService ticketsService;

    public void sendSlaMailGraph(String accessToken, Set<String> targetSet, String subject,
                                 Ticket ticket, String messageType) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();


        // Recipients JSON
        JSONArray targetMailArray = new JSONArray();

        for(String target:targetSet){
            targetMailArray.put(new JSONObject().put("emailAddress", new JSONObject().put("address", target)));
        }


        // InLine Images JSON
        JSONArray attachments = new JSONArray();
        Set<String> imagePaths = Set.of("LogoDelfos", "LogoEquatorial");

        for (String path : imagePaths){

            String imgBase64 = encodeImgToBase64(String.format("imgs/%s.png", path));
            JSONObject attachment = new JSONObject()
                    .put("@odata.type", "#microsoft.graph.fileAttachment")
                    .put("name", String.format("%s.png", path))
                    .put("contentBytes", imgBase64)
                    .put("contentId", String.format("<%s>", path))
                    .put("isInline", true);

            attachments.put(attachment);
        }

        //Data insertion

        Map<String, String> ticketData = Map.of(
                "numeroTicket", ticket.getId().toString(),
                "nome_do_modulo", ticket.getIndicator().getModule().getName(),
                "nome_do_indicador", ticket.getIndicator().getName(),
                "nome_do_solicitante", ticket.getUser().getName(),
                "data_de_abertura", ticket.getCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                "descricaoTicket", ticket.getDescription(),
                "link_visualizar_ticket", "https://google.com",
                "link_suporte", "https://google.com",
                "link_download", "https://google.com",
                "link_preferencias_email", "https://google.com"
        );

        String htmlFile = switch (messageType) {
            case "newTicketMail" -> loadHtml("templates/newTicket.html");
            case "slaMail" -> loadHtml("templates/sla.html");
            default -> loadHtml("templates/sla.html");
        };

        String finalHtml = insertTicketData(htmlFile, ticketData);

        // Full JSON build
        JSONObject message = new JSONObject()
                .put("message", new JSONObject()
                        .put("subject", subject)
                        .put("body", new JSONObject()
                                .put("contentType", "HTML")
                                .put("content", finalHtml))
                        .put("toRecipients", targetMailArray)
                        .put("attachments", attachments)
                )
                .put("saveToSentItems", true);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://graph.microsoft.com/v1.0/users/%s/sendMail", sourceMail)))
                .header("Authorization", "Bearer" + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(message.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400){
            log.warn("Erro no envio de email:\n{}: {} - Ticket {}", response.statusCode(), messageType,response.body());
        } else {
            log.info("Email enviado {} - {} Ticket {}", response.statusCode(), messageType, ticket.getId());
        }
    }

    public void directMail(String targetMailStr, String messageStr, String subject, String idStr){

        Set<String> targetSet = new HashSet<>(Arrays.asList(targetMailStr.split(",")));

        Ticket ticket = ticketsService.lerTicket(Long.parseLong(idStr));

        try {
            String accessToken = authService.getAccessToken();

            sendSlaMailGraph(accessToken, targetSet, subject, ticket, "directMail");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String loadHtml(String fileName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileNotFoundException("Arquivo HTML não encontrado: " + fileName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public String encodeImgToBase64(String resourcePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Imagem não encontrada: " + resourcePath);
            }
            byte[] bytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }

    public String insertTicketData(String html, Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            // Regex: substitui todas as ocorrências de {{chave}}
            html = html.replaceAll("\\{\\{" + Pattern.quote(entry.getKey()) + "\\}\\}", Matcher.quoteReplacement(entry.getValue()));
        }
        return html;
    }

    public Set<String> getTicketUsersMail(Ticket ticket){

        List<String> emailList = new ArrayList<>();

        emailList.add(ticket.getUser().getEmail());

        for (TicketAssignment assignment : ticket.getAssignments()){
            emailList.add(assignment.getUser().getEmail());
        }

        for (TicketForwarding forwarding : ticket.getForwardings()){
            emailList.add(forwarding.getUser().getEmail());
        }

        return new HashSet<>(emailList);
    }
}

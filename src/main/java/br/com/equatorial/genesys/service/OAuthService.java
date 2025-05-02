package br.com.equatorial.genesys.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@ApplicationScoped
public class OAuthService {

    @ConfigProperty(name = "azure.tenant-id")
    String tenantId;

    @ConfigProperty(name = "azure.client-id")
    String clientId;

    @ConfigProperty(name = "azure.client-secret")
    String clientSecret;

    public String getAccessToken() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String body = "client_id=" + clientId
                + "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default"
                + "&client_secret=" + clientSecret
                + "&grant_type=client_credentials";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject responseJson = new JSONObject(response.body());
        return responseJson.getString("access_token");
    }

}

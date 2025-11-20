package com.microfinance.workers_microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakService {

    @Value("${keycloak.url:http://keycloak:8085}")
    private String keycloakUrl;

    @Value("${keycloak.realm:microfinance-microservice-realm}")
    private String realm;

    @Autowired
    private RestTemplate restTemplate;

    public String createUserWithUserToken(
            String bearerToken,
            String username,
            String email,
            String firstName,
            String lastName,
            String password
    )
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("email", email);
        payload.put("enabled", true);
        payload.put("firstName", firstName);
        payload.put("lastName", lastName);

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);
        payload.put("credentials", List.of(credential));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String cleanToken = bearerToken.startsWith("Bearer ") ?
                bearerToken.substring(7) : bearerToken;
        headers.setBearerAuth(cleanToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakUrl + "/admin/realms/" + realm + "/users",
                request,
                Void.class
        );

        String location = response.getHeaders().getLocation().toString();
        return location.substring(location.lastIndexOf('/') + 1);
    }
    public void updateUserWithUserToken(
            String bearerToken,
            String keycloakId,
            String email,
            String firstName,
            String lastName,
            Boolean enabled
    ) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("firstName", firstName);
        payload.put("lastName", lastName);
        if (enabled != null) {
            payload.put("enabled", enabled);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String cleanToken = bearerToken.startsWith("Bearer ") ?
                bearerToken.substring(7) : bearerToken;
        headers.setBearerAuth(cleanToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakId;
        restTemplate.put(url, request);
    }

    public void updateUserWithUserTokenDynamic(String bearerToken, String keycloakId, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String cleanToken = bearerToken.startsWith("Bearer ") ?
                bearerToken.substring(7) : bearerToken;
        headers.setBearerAuth(cleanToken);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakId;
        restTemplate.put(url, request);
    }

}

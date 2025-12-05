package com.microfinance.workers_microservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class KeycloakService {

    @Value("${keycloak.url:http://keycloak:8085}")
    private String keycloakUrl;

    @Value("${keycloak.realm:microfinance-microservice-realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId; // cliente por defecto (p.ej. "frontend-client")

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    // =========================
    //   TOKEN ADMIN (CLIENT CREDENTIALS)
    // =========================

    private String cachedToken;
    private Instant cachedTokenExpiresAt;

    /**
     * Obtiene un access_token usando client_credentials.
     * Hace un cache sencillo para no pedirlo en cada request.
     */
    @CircuitBreaker(name = "keycloak-service", fallbackMethod = "getAdminAccessTokenFallback")
    private synchronized String getAdminAccessToken() {
        if (cachedToken != null && cachedTokenExpiresAt != null) {
            // Le dejamos un margen de 30 segundos antes de expirar
            if (Instant.now().isBefore(cachedTokenExpiresAt.minusSeconds(30))) {
                return cachedToken;
            }
        }

        String tokenEndpoint = keycloakUrl
                + "/realms/" + realm
                + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> req =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> resp = restTemplate.postForEntity(
                tokenEndpoint,
                req,
                Map.class
        );

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("No se pudo obtener access_token de Keycloak (client_credentials)");
        }

        Map<String, Object> respBody = resp.getBody();
        String accessToken = Objects.toString(respBody.get("access_token"), null);
        Number expiresIn = (Number) respBody.getOrDefault("expires_in", 60);

        if (accessToken == null) {
            throw new IllegalStateException("Keycloak no devolvió access_token");
        }

        cachedToken = accessToken;
        cachedTokenExpiresAt = Instant.now().plusSeconds(expiresIn.longValue());

        return accessToken;
    }
    @SuppressWarnings("unused")
    private String getAdminAccessTokenFallback(Throwable ex) {
        throw new IllegalStateException("Keycloak no disponible al obtener admin token", ex);
    }

    private HttpHeaders buildJsonHeadersWithAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAdminAccessToken());
        return headers;
    }

    // =========================
    //   USUARIO
    // =========================

    /**
     * Crea un usuario en Keycloak usando el token ADMIN (client_credentials),
     * ignorando el bearerToken del usuario.
     */
    @CircuitBreaker(name = "keycloak-service", fallbackMethod = "createUserWithUserTokenFallback")
    public String createUserWithUserToken(
            String bearerToken,
            String username,
            String email,
            String firstName,
            String lastName,
            String password
    ) {
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

        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakUrl + "/admin/realms/" + realm + "/users",
                request,
                Void.class
        );

        String location = response.getHeaders().getLocation() != null
                ? response.getHeaders().getLocation().toString()
                : null;

        if (location == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Error creando usuario en Keycloak");
        }

        return location.substring(location.lastIndexOf('/') + 1);
    }
    @SuppressWarnings("unused")
    private String createUserWithUserTokenFallback(
            String bearerToken,
            String username,
            String email,
            String firstName,
            String lastName,
            String password,
            Throwable ex
    ) {
        throw new IllegalStateException("Keycloak no disponible al crear usuario " + username, ex);
    }

    /**
     * 🔥 NUEVO: Crea usuario en Keycloak con ID específico (para recrear usuarios)
     */
    public String createUserWithUserTokenAndId(
            String bearerToken,
            String keycloakId,
            String username,
            String email,
            String firstName,
            String lastName,
            String password
    ) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", keycloakId);              // 🔥 Forzar ID específico
        payload.put("username", username);
        payload.put("email", email);
        payload.put("emailVerified", false);        // 🔥 NECESARIO para que respete el ID
        payload.put("enabled", true);
        payload.put("firstName", firstName);
        payload.put("lastName", lastName);

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);         // 🔥 NO usar temporary=true cuando se forza ID
        payload.put("credentials", List.of(credential));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));    // 🔥 NECESARIO
        headers.setBearerAuth(getAdminAccessToken());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakUrl + "/admin/realms/" + realm + "/users",
                request,
                Void.class
        );

        // 🔍 Verificar que Keycloak RESPETÓ tu ID
        String location = response.getHeaders().getLocation() != null
                ? response.getHeaders().getLocation().toString()
                : null;

        if (location == null) {
            throw new IllegalStateException("Keycloak no devolvió Location al crear usuario");
        }

        // Extraer ID real del Location
        String realId = location.substring(location.lastIndexOf('/') + 1);

        if (!realId.equals(keycloakId)) {
            throw new IllegalStateException("Keycloak ignoró el ID. Generó otro: " + realId);
        }

        return keycloakId;
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

        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakId;
        restTemplate.put(url, request);
    }

    public void updateUserWithUserTokenDynamic(String bearerToken, String keycloakId, Map<String, Object> payload) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakId;
        restTemplate.put(url, request);
    }

    // =========================
    //   ROLES (READ)
    // =========================

    /**
     * Obtiene la definición de un Realm Role por nombre.
     */
    public Map<String, Object> getRealmRole(String bearerToken, String roleName) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
        return resp.getBody();
    }

    /**
     * Obtiene el ID interno (UUID) del cliente (by clientId).
     */
    private String getClientUuid(String clientIdToUse) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/clients?clientId=" + clientIdToUse;
        ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, req, List.class);
        List<?> body = resp.getBody();
        if (body == null || body.isEmpty()) {
            throw new IllegalStateException("No se encontró el cliente en Keycloak: " + clientIdToUse);
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> client = (Map<String, Object>) body.get(0);
        Object idObj = client.get("id");
        if (idObj == null) {
            throw new IllegalStateException("El cliente no tiene id en la respuesta de Keycloak");
        }
        return idObj.toString();
    }

    /**
     * Obtiene la definición de un Client Role por nombre.
     */
    public Map<String, Object> getClientRole(String bearerToken, String clientUuid, String roleName) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String url = keycloakUrl
                + "/admin/realms/" + realm
                + "/clients/" + clientUuid
                + "/roles/" + roleName;

        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
        return resp.getBody();
    }

    // =========================
    //   ROLES (ASSIGN / REMOVE)
    // =========================

    public void assignRolesToUser(
            String bearerToken,
            String keycloakUserId,
            String realmRoleName,
            String clientRoleName,
            String clientIdOverride
    ) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();

        // Realm role
        if (realmRoleName != null && !realmRoleName.isBlank()) {
            Map<String, Object> realmRole = getRealmRole(bearerToken, realmRoleName);
            if (realmRole != null) {
                HttpEntity<List<Map<String, Object>>> req =
                        new HttpEntity<>(List.of(realmRole), headers);

                String url = keycloakUrl
                        + "/admin/realms/" + realm
                        + "/users/" + keycloakUserId
                        + "/role-mappings/realm";

                restTemplate.postForEntity(url, req, Void.class);
            }
        }

        // Client role
        if (clientRoleName != null && !clientRoleName.isBlank()) {
            String effectiveClientId = (clientIdOverride != null && !clientIdOverride.isBlank())
                    ? clientIdOverride
                    : this.clientId;

            String clientUuid = getClientUuid(effectiveClientId);
            Map<String, Object> clientRole = getClientRole(bearerToken, clientUuid, clientRoleName);
            if (clientRole != null) {
                HttpEntity<List<Map<String, Object>>> req =
                        new HttpEntity<>(List.of(clientRole), headers);

                String url = keycloakUrl
                        + "/admin/realms/" + realm
                        + "/users/" + keycloakUserId
                        + "/role-mappings/clients/" + clientUuid;

                restTemplate.postForEntity(url, req, Void.class);
            }
        }
    }

    // Overload para código existente (usa clientId por defecto)
    public void assignRolesToUser(
            String bearerToken,
            String keycloakUserId,
            String realmRoleName,
            String clientRoleName
    ) {
        assignRolesToUser(bearerToken, keycloakUserId, realmRoleName, clientRoleName, null);
    }

    public void removeRolesFromUser(
            String bearerToken,
            String keycloakUserId,
            String realmRoleName,
            String clientRoleName,
            String clientIdOverride
    ) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();

        // Realm role
        if (realmRoleName != null && !realmRoleName.isBlank()) {
            Map<String, Object> realmRole = getRealmRole(bearerToken, realmRoleName);
            if (realmRole != null) {
                HttpEntity<List<Map<String, Object>>> req =
                        new HttpEntity<>(List.of(realmRole), headers);

                String url = keycloakUrl
                        + "/admin/realms/" + realm
                        + "/users/" + keycloakUserId
                        + "/role-mappings/realm";

                restTemplate.exchange(url, HttpMethod.DELETE, req, Void.class);
            }
        }

        // Client role
        if (clientRoleName != null && !clientRoleName.isBlank()) {
            String effectiveClientId = (clientIdOverride != null && !clientIdOverride.isBlank())
                    ? clientIdOverride
                    : this.clientId;

            String clientUuid = getClientUuid(effectiveClientId);
            Map<String, Object> clientRole = getClientRole(bearerToken, clientUuid, clientRoleName);
            if (clientRole != null) {
                HttpEntity<List<Map<String, Object>>> req =
                        new HttpEntity<>(List.of(clientRole), headers);

                String url = keycloakUrl
                        + "/admin/realms/" + realm
                        + "/users/" + keycloakUserId
                        + "/role-mappings/clients/" + clientUuid;

                restTemplate.exchange(url, HttpMethod.DELETE, req, Void.class);
            }
        }
    }

    // Overload para código existente (usa clientId por defecto)
    public void removeRolesFromUser(
            String bearerToken,
            String keycloakUserId,
            String realmRoleName,
            String clientRoleName
    ) {
        removeRolesFromUser(bearerToken, keycloakUserId, realmRoleName, clientRoleName, null);
    }

    // =========================
    //   MÉTODOS NUEVOS / LISTADOS
    // =========================

    /**
     * Lista usuarios del realm (hasta un máximo razonable).
     */
    public List<Map<String, Object>> listAllUsers(String bearerToken) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String url = keycloakUrl
                + "/admin/realms/" + realm
                + "/users?max=1000";

        ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, req, List.class);
        List body = resp.getBody();
        if (body == null) {
            return List.of();
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = (List<Map<String, Object>>) (List<?>) body;

        // Log de depuración
        System.out.println("🔍 KeycloakService.listAllUsers -> " + users.size() + " usuarios encontrados");

        for (Map<String, Object> user : users) {
            String userId = Objects.toString(user.get("id"), "unknown");
            String username = Objects.toString(user.get("username"), "unknown");

            try {
                List<String> realmRoles = getUserRealmRoles(bearerToken, userId);
                List<String> clientRoles = getUserClientRoles(bearerToken, userId);

                System.out.println("👤 Usuario: " + username +
                        " | Realm Roles: " + realmRoles +
                        " | Client Roles: " + clientRoles);
            } catch (Exception e) {
                System.out.println("❌ Error obteniendo roles para usuario: " + username);
            }
        }

        return users;
    }

    /**
     * Obtiene el detalle de un usuario por ID.
     */
    public Map<String, Object> getUserById(String bearerToken, String userId) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String url = keycloakUrl
                + "/admin/realms/" + realm
                + "/users/" + userId;

        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, req, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = resp.getBody();
        return body != null ? body : Map.of();
    }

    /**
     * 🔥 NUEVO: Verifica si un usuario existe en Keycloak
     */
    public boolean userExists(String bearerToken, String keycloakId) {
        try {
            Map<String, Object> user = getUserById(bearerToken, keycloakId);
            return user != null && !user.isEmpty() && user.get("id") != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lista los Realm Roles asignados a un usuario.
     */
    public List<String> getUserRealmRoles(String bearerToken, String userId) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String url = keycloakUrl
                + "/admin/realms/" + realm
                + "/users/" + userId
                + "/role-mappings/realm";

        ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, req, List.class);
        List body = resp.getBody();
        if (body == null) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> roles = (List<Map<String, Object>>) (List<?>) body;

        return roles.stream()
                .map(r -> Objects.toString(r.get("name"), null))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Lista los Client Roles (del cliente principal) asignados a un usuario.
     * Usa keycloak.client-id como cliente base.
     */
    public List<String> getUserClientRoles(String bearerToken, String userId) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String clientUuid = getClientUuid(this.clientId);

        String url = keycloakUrl
                + "/admin/realms/" + realm
                + "/users/" + userId
                + "/role-mappings/clients/" + clientUuid;

        ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, req, List.class);
        List body = resp.getBody();
        if (body == null) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> roles = (List<Map<String, Object>>) (List<?>) body;

        return roles.stream()
                .map(r -> Objects.toString(r.get("name"), null))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 🔥 NUEVO: Elimina usuario de Keycloak
     */
    public void deleteUser(String bearerToken, String keycloakId) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakId;
        
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
        } catch (Exception e) {
            throw new IllegalStateException("Error eliminando usuario de Keycloak: " + e.getMessage());
        }
    }

    /**
     * 🔥 NUEVO: Busca usuario por username
     */
    public Map<String, Object> getUserByUsername(String bearerToken, String username) {
        HttpHeaders headers = buildJsonHeadersWithAdminToken();
        HttpEntity<Void> req = new HttpEntity<>(headers);

        String url = keycloakUrl
                + "/admin/realms/" + realm
                + "/users?username=" + username;

        ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, req, List.class);
        List body = resp.getBody();
        if (body == null || body.isEmpty()) {
            return Map.of();
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = (List<Map<String, Object>>) (List<?>) body;
        
        return users.stream()
                .filter(user -> Objects.equals(user.get("username"), username))
                .findFirst()
                .orElse(Map.of());
    }
}
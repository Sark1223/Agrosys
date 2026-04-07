package com.agrosys.web.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.agrosys.web.dto.login.LoginRequest;
import com.agrosys.web.dto.login.LoginResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${gateway.base-url}")
    private String gatewayUrl;

    @SuppressWarnings("null")
    public LoginResponse login(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUserName(username);
        request.setPassword(password);

        log.info("Attempting to authenticate user: {}", request.getUserName());

        try {
            return webClientBuilder.build()
                    .post()
                    .uri(gatewayUrl + "/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(request))
                    .retrieve()
                    .bodyToMono(LoginResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error calling auth service: {}", e.getMessage());
            throw new RuntimeException("Authentication failed", e);
        }
    }

    @SuppressWarnings("null")
    public <T> T get(String path, Class<T> responseType, String token) {
        return webClientBuilder.build()
                .get()
                .uri(gatewayUrl + path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @SuppressWarnings("null")
    public <T> T post(String path, Object request, Class<T> responseType, String token) {
        return webClientBuilder.build()
                .post()
                .uri(gatewayUrl + path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @SuppressWarnings("null")
    public <T> T put(String path, Object request, Class<T> responseType, String token) {
        return webClientBuilder.build()
                .put()
                .uri(gatewayUrl + path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @SuppressWarnings("null")
    public <T> T patch(String path, Object request, Class<T> responseType, String token) {
        return webClientBuilder.build()
                .patch()
                .uri(gatewayUrl + path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @SuppressWarnings("null")
    public <T> T delete(String path, Object request, Class<T> responseType, String token) {
        return webClientBuilder.build()
                .method(HttpMethod.DELETE) 
                .uri(gatewayUrl + path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
package com.activity.activityservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        try {
            // Call the User Service to validate the user
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            // Handle exceptions (e.g., service unavailable, timeout)
            e.printStackTrace();
            return false;
        }
    }
}

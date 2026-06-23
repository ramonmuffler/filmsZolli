package org.example.films.user;

import java.time.Instant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        Instant registrationDate
) {
}

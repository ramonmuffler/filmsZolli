package org.example.films.watched;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public record WatchedRequest(
        @NotBlank String userId,
        @NotBlank String contentId,
        Instant watchedDate
) {
}

package org.example.films.favorite;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;

public record FavoriteRequest(
        @NotBlank String userId,
        @NotBlank String contentId,
        Instant favoriteDate
) {
}

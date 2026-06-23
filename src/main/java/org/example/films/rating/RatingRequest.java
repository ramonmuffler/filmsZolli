package org.example.films.rating;

import java.time.Instant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RatingRequest(
        @NotBlank String userId,
        @NotBlank String contentId,
        @Min(1) @Max(5) int stars,
        String comment,
        Instant ratingDate
) {
}

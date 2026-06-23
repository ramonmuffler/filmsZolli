package org.example.films.content;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContentRequest(
        @NotBlank String title,
        String description,
        @Min(1888) @Max(2100) int releaseYear,
        @NotNull ContentType type,
        List<String> categoryIds
) {
    public List<String> normalizedCategoryIds() {
        if (categoryIds == null) {
            return List.of();
        }
        return categoryIds.stream()
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
    }
}

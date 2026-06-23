package org.example.films.rating;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Rating> findAll(
            @RequestParam(required = false) String contentId,
            @RequestParam(required = false) String userId
    ) {
        return ratingService.findAll(contentId, userId);
    }

    @GetMapping("/{id}")
    public Rating get(@PathVariable String id) {
        return ratingService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rating create(@Valid @RequestBody RatingRequest request) {
        return ratingService.create(request);
    }

    @PutMapping("/{id}")
    public Rating update(@PathVariable String id, @Valid @RequestBody RatingRequest request) {
        return ratingService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        ratingService.delete(id);
    }
}

package org.example.films.favorite;

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
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<Favorite> findAll(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String contentId
    ) {
        return favoriteService.findAll(userId, contentId);
    }

    @GetMapping("/{id}")
    public Favorite get(@PathVariable String id) {
        return favoriteService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Favorite create(@Valid @RequestBody FavoriteRequest request) {
        return favoriteService.create(request);
    }

    @PutMapping("/{id}")
    public Favorite update(@PathVariable String id, @Valid @RequestBody FavoriteRequest request) {
        return favoriteService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        favoriteService.delete(id);
    }
}

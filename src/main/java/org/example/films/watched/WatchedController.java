package org.example.films.watched;

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
@RequestMapping("/api/watched")
public class WatchedController {

    private final WatchedService watchedService;

    public WatchedController(WatchedService watchedService) {
        this.watchedService = watchedService;
    }

    @GetMapping
    public List<Watched> findAll(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String contentId
    ) {
        return watchedService.findAll(userId, contentId);
    }

    @GetMapping("/{id}")
    public Watched get(@PathVariable String id) {
        return watchedService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Watched create(@Valid @RequestBody WatchedRequest request) {
        return watchedService.create(request);
    }

    @PutMapping("/{id}")
    public Watched update(@PathVariable String id, @Valid @RequestBody WatchedRequest request) {
        return watchedService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        watchedService.delete(id);
    }
}

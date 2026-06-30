package org.example.films.user;

import java.time.Instant;
import java.util.List;

import org.example.films.common.NotFoundException;
import org.example.films.content.ContentService;
import org.example.films.favorite.FavoriteRepository;
import org.example.films.rating.RatingRepository;
import org.example.films.watched.WatchedRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final WatchedRepository watchedRepository;
    private final FavoriteRepository favoriteRepository;
    private final ContentService contentService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(
            UserRepository userRepository,
            RatingRepository ratingRepository,
            WatchedRepository watchedRepository,
            FavoriteRepository favoriteRepository,
            ContentService contentService
    ) {
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.watchedRepository = watchedRepository;
        this.favoriteRepository = favoriteRepository;
        this.contentService = contentService;
    }

    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    public AppUser get(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Benutzer", id));
    }

    public AppUser create(UserRequest request) {
        ensureUnique(request.username(), request.email(), null);
        AppUser user = new AppUser();
        apply(user, request);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return userRepository.save(user);
    }

    public AppUser update(String id, UserRequest request) {
        AppUser user = get(id);
        ensureUnique(request.username(), request.email(), id);
        apply(user, request);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return userRepository.save(user);
    }

    public void delete(String id) {
        AppUser user = get(id);
        List<String> affectedContentIds = ratingRepository.findByUserId(id).stream()
                .map(rating -> rating.getContentId())
                .distinct()
                .toList();
        ratingRepository.deleteByUserId(id);
        watchedRepository.deleteByUserId(id);
        favoriteRepository.deleteByUserId(id);
        userRepository.delete(user);
        affectedContentIds.forEach(contentService::updateAverageRating);
    }

    private void apply(AppUser user, UserRequest request) {
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim());
        user.setRegistrationDate(request.registrationDate() == null ? Instant.now() : request.registrationDate());
    }

    private void ensureUnique(String username, String email, String ownId) {
        userRepository.findByUsernameIgnoreCase(username.trim())
                .filter(existing -> !existing.getId().equals(ownId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Dieser Username ist bereits vergeben.");
                });
        userRepository.findByEmailIgnoreCase(email.trim())
                .filter(existing -> !existing.getId().equals(ownId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Diese E-Mail ist bereits registriert.");
                });
    }
}

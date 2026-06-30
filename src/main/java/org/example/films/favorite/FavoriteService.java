package org.example.films.favorite;

import java.time.Instant;
import java.util.List;

import org.example.films.common.NotFoundException;
import org.example.films.content.ContentRepository;
import org.example.films.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            ContentRepository contentRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    public List<Favorite> findAll(String userId, String contentId) {
        if (userId != null && !userId.isBlank()) {
            return favoriteRepository.findByUserId(userId);
        }
        if (contentId != null && !contentId.isBlank()) {
            return favoriteRepository.findByContentId(contentId);
        }
        return favoriteRepository.findAll();
    }

    public Favorite get(String id) {
        return favoriteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Favorit", id));
    }

    public Favorite create(FavoriteRequest request) {
        validateReferences(request.userId(), request.contentId());
        favoriteRepository.findByUserIdAndContentId(request.userId(), request.contentId())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Dieser Inhalt ist bereits in der Favoritenliste.");
                });
        Favorite favorite = new Favorite();
        apply(favorite, request);
        return favoriteRepository.save(favorite);
    }

    public Favorite update(String id, FavoriteRequest request) {
        validateReferences(request.userId(), request.contentId());
        favoriteRepository.findByUserIdAndContentId(request.userId(), request.contentId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Dieser Inhalt ist bereits in der Favoritenliste.");
                });
        Favorite favorite = get(id);
        apply(favorite, request);
        return favoriteRepository.save(favorite);
    }

    public void delete(String id) {
        favoriteRepository.delete(get(id));
    }

    private void apply(Favorite favorite, FavoriteRequest request) {
        favorite.setUserId(request.userId());
        favorite.setContentId(request.contentId());
        favorite.setFavoriteDate(request.favoriteDate() == null ? Instant.now() : request.favoriteDate());
    }

    private void validateReferences(String userId, String contentId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Benutzer", userId);
        }
        if (!contentRepository.existsById(contentId)) {
            throw new NotFoundException("Inhalt", contentId);
        }
    }
}

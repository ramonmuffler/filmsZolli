package org.example.films.rating;

import java.time.Instant;
import java.util.List;

import org.example.films.common.NotFoundException;
import org.example.films.content.ContentRepository;
import org.example.films.content.ContentService;
import org.example.films.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final ContentService contentService;

    public RatingService(
            RatingRepository ratingRepository,
            UserRepository userRepository,
            ContentRepository contentRepository,
            ContentService contentService
    ) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.contentService = contentService;
    }

    public List<Rating> findAll(String contentId, String userId) {
        if (contentId != null && !contentId.isBlank()) {
            return ratingRepository.findByContentId(contentId);
        }
        if (userId != null && !userId.isBlank()) {
            return ratingRepository.findByUserId(userId);
        }
        return ratingRepository.findAll();
    }

    public Rating get(String id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bewertung", id));
    }

    public Rating create(RatingRequest request) {
        validateReferences(request.userId(), request.contentId());
        Rating rating = new Rating();
        apply(rating, request);
        Rating saved = ratingRepository.save(rating);
        contentService.updateAverageRating(saved.getContentId());
        return saved;
    }

    public Rating update(String id, RatingRequest request) {
        Rating rating = get(id);
        String previousContentId = rating.getContentId();
        validateReferences(request.userId(), request.contentId());
        apply(rating, request);
        Rating saved = ratingRepository.save(rating);
        contentService.updateAverageRating(previousContentId);
        contentService.updateAverageRating(saved.getContentId());
        return saved;
    }

    public void delete(String id) {
        Rating rating = get(id);
        String contentId = rating.getContentId();
        ratingRepository.delete(rating);
        contentService.updateAverageRating(contentId);
    }

    private void apply(Rating rating, RatingRequest request) {
        rating.setUserId(request.userId());
        rating.setContentId(request.contentId());
        rating.setStars(request.stars());
        rating.setComment(request.comment());
        rating.setRatingDate(request.ratingDate() == null ? Instant.now() : request.ratingDate());
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

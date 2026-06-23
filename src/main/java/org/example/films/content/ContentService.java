package org.example.films.content;

import java.util.List;

import org.example.films.category.CategoryRepository;
import org.example.films.common.NotFoundException;
import org.example.films.rating.Rating;
import org.example.films.rating.RatingRepository;
import org.example.films.watched.WatchedRepository;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final WatchedRepository watchedRepository;

    public ContentService(
            ContentRepository contentRepository,
            CategoryRepository categoryRepository,
            RatingRepository ratingRepository,
            WatchedRepository watchedRepository
    ) {
        this.contentRepository = contentRepository;
        this.categoryRepository = categoryRepository;
        this.ratingRepository = ratingRepository;
        this.watchedRepository = watchedRepository;
    }

    public List<Content> findAll(String categoryId) {
        if (categoryId != null && !categoryId.isBlank()) {
            return contentRepository.findByCategoryIdsContaining(categoryId);
        }
        return contentRepository.findAll();
    }

    public Content get(String id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inhalt", id));
    }

    public Content create(ContentRequest request) {
        Content content = new Content();
        apply(content, request);
        return contentRepository.save(content);
    }

    public Content update(String id, ContentRequest request) {
        Content content = get(id);
        apply(content, request);
        return contentRepository.save(content);
    }

    public void delete(String id) {
        Content content = get(id);
        ratingRepository.deleteByContentId(id);
        watchedRepository.deleteByContentId(id);
        contentRepository.delete(content);
    }

    public void updateAverageRating(String contentId) {
        contentRepository.findById(contentId).ifPresent(content -> {
            double average = ratingRepository.findByContentId(contentId).stream()
                    .mapToInt(Rating::getStars)
                    .average()
                    .orElse(0.0);
            content.setAverageRating(Math.round(average * 10.0) / 10.0);
            contentRepository.save(content);
        });
    }

    private void apply(Content content, ContentRequest request) {
        List<String> categoryIds = request.normalizedCategoryIds();
        validateCategories(categoryIds);
        content.setTitle(request.title().trim());
        content.setDescription(request.description());
        content.setReleaseYear(request.releaseYear());
        content.setType(request.type());
        content.setCategoryIds(categoryIds);
    }

    private void validateCategories(List<String> categoryIds) {
        for (String categoryId : categoryIds) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new NotFoundException("Kategorie", categoryId);
            }
        }
    }
}

package org.example.films.config;

import java.time.Instant;
import java.util.List;

import org.example.films.category.Category;
import org.example.films.category.CategoryRepository;
import org.example.films.content.Content;
import org.example.films.content.ContentRepository;
import org.example.films.content.ContentType;
import org.example.films.favorite.Favorite;
import org.example.films.favorite.FavoriteRepository;
import org.example.films.rating.Rating;
import org.example.films.rating.RatingRepository;
import org.example.films.user.AppUser;
import org.example.films.user.UserRepository;
import org.example.films.watched.Watched;
import org.example.films.watched.WatchedRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SampleDataInitializer implements ApplicationRunner {

    private static final String ACTION_ID = "665f1a1b2c3d4e5f67890201";
    private static final String SCIFI_ID = "665f1a1b2c3d4e5f67890202";
    private static final String DRAMA_ID = "665f1a1b2c3d4e5f67890203";
    private static final String COMEDY_ID = "665f1a1b2c3d4e5f67890204";
    private static final String TIMO_ID = "665f1a1b2c3d4e5f67890401";
    private static final String RAMON_ID = "665f1a1b2c3d4e5f67890402";
    private static final String INCEPTION_ID = "665f1a1b2c3d4e5f67890101";
    private static final String BREAKING_BAD_ID = "665f1a1b2c3d4e5f67890102";
    private static final String GRAND_BUDAPEST_ID = "665f1a1b2c3d4e5f67890103";

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final RatingRepository ratingRepository;
    private final WatchedRepository watchedRepository;
    private final FavoriteRepository favoriteRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SampleDataInitializer(
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            ContentRepository contentRepository,
            RatingRepository ratingRepository,
            WatchedRepository watchedRepository,
            FavoriteRepository favoriteRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.ratingRepository = ratingRepository;
        this.watchedRepository = watchedRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        saveCategory(ACTION_ID, "Action");
        saveCategory(SCIFI_ID, "Sci-Fi");
        saveCategory(DRAMA_ID, "Drama");
        saveCategory(COMEDY_ID, "Comedy");

        saveUser(TIMO_ID, "timomaag", "timo@example.ch", "2026-06-01T08:00:00Z");
        saveUser(RAMON_ID, "ramonmuffler", "ramon@example.ch", "2026-06-01T08:15:00Z");

        saveContent(
                INCEPTION_ID,
                "Inception",
                "Ein Dieb stiehlt Informationen aus den Traeumen anderer Menschen.",
                2010,
                ContentType.FILM,
                List.of(ACTION_ID, SCIFI_ID),
                4.5
        );
        saveContent(
                BREAKING_BAD_ID,
                "Breaking Bad",
                "Ein Chemielehrer steigt in die Produktion illegaler Substanzen ein.",
                2008,
                ContentType.SERIE,
                List.of(DRAMA_ID),
                5.0
        );
        saveContent(
                GRAND_BUDAPEST_ID,
                "The Grand Budapest Hotel",
                "Ein Concierge und ein Lobby Boy geraten in ein kurioses Abenteuer.",
                2014,
                ContentType.FILM,
                List.of(COMEDY_ID, DRAMA_ID),
                4.0
        );

        saveRating("665f1a1b2c3d4e5f67890301", TIMO_ID, INCEPTION_ID, 5,
                "Sehr spannender Film mit guter Handlung.", "2026-06-09T10:00:00Z");
        saveRating("665f1a1b2c3d4e5f67890302", RAMON_ID, INCEPTION_ID, 4,
                "Guter Film, teilweise etwas kompliziert.", "2026-06-09T11:30:00Z");
        saveRating("665f1a1b2c3d4e5f67890303", TIMO_ID, BREAKING_BAD_ID, 5,
                "Sehr starke Serie.", "2026-06-10T19:00:00Z");
        saveRating("665f1a1b2c3d4e5f67890304", RAMON_ID, GRAND_BUDAPEST_ID, 4,
                "Schoen gemacht und unterhaltsam.", "2026-06-11T20:00:00Z");

        saveWatched("665f1a1b2c3d4e5f67890501", TIMO_ID, INCEPTION_ID, "2026-06-08T20:15:00Z");
        saveWatched("665f1a1b2c3d4e5f67890502", RAMON_ID, BREAKING_BAD_ID, "2026-06-12T18:45:00Z");

        saveFavorite("665f1a1b2c3d4e5f67890601", TIMO_ID, INCEPTION_ID, "2026-06-09T12:00:00Z");
        saveFavorite("665f1a1b2c3d4e5f67890602", RAMON_ID, BREAKING_BAD_ID, "2026-06-12T19:00:00Z");
    }

    private void saveCategory(String id, String name) {
        if (categoryRepository.existsById(id)) {
            return;
        }
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        categoryRepository.save(category);
    }

    private void saveUser(String id, String username, String email, String registrationDate) {
        if (userRepository.existsById(id)) {
            return;
        }
        AppUser user = new AppUser();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("passwort123"));
        user.setRegistrationDate(Instant.parse(registrationDate));
        userRepository.save(user);
    }

    private void saveContent(
            String id,
            String title,
            String description,
            int releaseYear,
            ContentType type,
            List<String> categoryIds,
            double averageRating
    ) {
        if (contentRepository.existsById(id)) {
            return;
        }
        Content content = new Content();
        content.setId(id);
        content.setTitle(title);
        content.setDescription(description);
        content.setReleaseYear(releaseYear);
        content.setType(type);
        content.setCategoryIds(categoryIds);
        content.setAverageRating(averageRating);
        contentRepository.save(content);
    }

    private void saveRating(String id, String userId, String contentId, int stars, String comment, String ratingDate) {
        if (ratingRepository.existsById(id)) {
            return;
        }
        Rating rating = new Rating();
        rating.setId(id);
        rating.setUserId(userId);
        rating.setContentId(contentId);
        rating.setStars(stars);
        rating.setComment(comment);
        rating.setRatingDate(Instant.parse(ratingDate));
        ratingRepository.save(rating);
    }

    private void saveWatched(String id, String userId, String contentId, String watchedDate) {
        if (watchedRepository.existsById(id)) {
            return;
        }
        Watched watched = new Watched();
        watched.setId(id);
        watched.setUserId(userId);
        watched.setContentId(contentId);
        watched.setWatchedDate(Instant.parse(watchedDate));
        watchedRepository.save(watched);
    }

    private void saveFavorite(String id, String userId, String contentId, String favoriteDate) {
        if (favoriteRepository.existsById(id)) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setId(id);
        favorite.setUserId(userId);
        favorite.setContentId(contentId);
        favorite.setFavoriteDate(Instant.parse(favoriteDate));
        favoriteRepository.save(favorite);
    }
}

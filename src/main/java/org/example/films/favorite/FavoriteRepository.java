package org.example.films.favorite;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {

    List<Favorite> findByUserId(String userId);

    List<Favorite> findByContentId(String contentId);

    Optional<Favorite> findByUserIdAndContentId(String userId, String contentId);

    void deleteByUserId(String userId);

    void deleteByContentId(String contentId);
}

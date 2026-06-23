package org.example.films.rating;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RatingRepository extends MongoRepository<Rating, String> {

    List<Rating> findByContentId(String contentId);

    List<Rating> findByUserId(String userId);

    void deleteByContentId(String contentId);

    void deleteByUserId(String userId);
}

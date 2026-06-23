package org.example.films.watched;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WatchedRepository extends MongoRepository<Watched, String> {

    List<Watched> findByUserId(String userId);

    List<Watched> findByContentId(String contentId);

    void deleteByUserId(String userId);

    void deleteByContentId(String contentId);
}

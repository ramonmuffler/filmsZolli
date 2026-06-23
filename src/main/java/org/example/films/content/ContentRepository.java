package org.example.films.content;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepository extends MongoRepository<Content, String> {

    List<Content> findByCategoryIdsContaining(String categoryId);
}

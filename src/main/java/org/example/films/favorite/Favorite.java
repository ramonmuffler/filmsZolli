package org.example.films.favorite;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("favorites")
@CompoundIndex(name = "userId_1_contentId_1", def = "{'userId': 1, 'contentId': 1}", unique = true)
public class Favorite {

    @Id
    private String id;

    private String userId;
    private String contentId;
    private Instant favoriteDate;

    public Favorite() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Instant getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(Instant favoriteDate) {
        this.favoriteDate = favoriteDate;
    }
}

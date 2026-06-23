package org.example.films.watched;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("watched")
public class Watched {

    @Id
    private String id;

    private String userId;
    private String contentId;
    private Instant watchedDate;

    public Watched() {
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

    public Instant getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(Instant watchedDate) {
        this.watchedDate = watchedDate;
    }
}

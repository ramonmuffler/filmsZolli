package org.example.films.watched;

import java.time.Instant;
import java.util.List;

import org.example.films.common.NotFoundException;
import org.example.films.content.ContentRepository;
import org.example.films.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class WatchedService {

    private final WatchedRepository watchedRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public WatchedService(
            WatchedRepository watchedRepository,
            UserRepository userRepository,
            ContentRepository contentRepository
    ) {
        this.watchedRepository = watchedRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    public List<Watched> findAll(String userId, String contentId) {
        if (userId != null && !userId.isBlank()) {
            return watchedRepository.findByUserId(userId);
        }
        if (contentId != null && !contentId.isBlank()) {
            return watchedRepository.findByContentId(contentId);
        }
        return watchedRepository.findAll();
    }

    public Watched get(String id) {
        return watchedRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gesehen-Eintrag", id));
    }

    public Watched create(WatchedRequest request) {
        validateReferences(request.userId(), request.contentId());
        Watched watched = new Watched();
        apply(watched, request);
        return watchedRepository.save(watched);
    }

    public Watched update(String id, WatchedRequest request) {
        validateReferences(request.userId(), request.contentId());
        Watched watched = get(id);
        apply(watched, request);
        return watchedRepository.save(watched);
    }

    public void delete(String id) {
        watchedRepository.delete(get(id));
    }

    private void apply(Watched watched, WatchedRequest request) {
        watched.setUserId(request.userId());
        watched.setContentId(request.contentId());
        watched.setWatchedDate(request.watchedDate() == null ? Instant.now() : request.watchedDate());
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

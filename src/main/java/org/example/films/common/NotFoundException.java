package org.example.films.common;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, String id) {
        super(resource + " mit ID " + id + " wurde nicht gefunden.");
    }
}

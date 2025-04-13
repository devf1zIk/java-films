package ru.yandex.practicum.filmorate.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotFoundException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(NotFoundException.class);

    public NotFoundException(final String message) {
        super(message);
        log.error("Не найдено: {}", message);
    }
}
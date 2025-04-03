package ru.yandex.practicum.filmorate.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(ValidateException.class);

    public ValidateException(final String message) {
        super(message);
        log.error("Ошибка валидации: {}", message);
    }
}

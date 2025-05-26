package de.spl12.domain.Exceptions.SessionHandlingExceptions;

/**
 * Indicates that the session was not found.
 */
public class SessionNotFoundException extends SessionHandlingException{
    public SessionNotFoundException(String message) {
        super(message);
    }
}

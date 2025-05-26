package de.spl12.domain.Exceptions.SessionHandlingExceptions;

/**
 * Indicates that the session is already full.
 */
public class SessionAlreadyFullException extends SessionHandlingException {
    public SessionAlreadyFullException(String message) {
        super(message);
    }
}

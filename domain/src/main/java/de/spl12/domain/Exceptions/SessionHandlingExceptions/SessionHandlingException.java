package de.spl12.domain.Exceptions.SessionHandlingExceptions;

/**
 * Base class for all exceptions that occur during the handling of a session.
 */
public class SessionHandlingException extends Exception {
    public SessionHandlingException(String message) {
        super(message);
    }
}

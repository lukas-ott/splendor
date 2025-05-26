package de.spl12.domain.Exceptions.SessionHandlingExceptions;

/**
 * Thrown to indicate that an attempt to start a session has been made
 * when the session has already been started.
 *
 * This exception is a specific subtype of {@link SessionHandlingException}
 * and is typically used in scenarios where operations are dependent
 * on the assumption that the session is not already active.
 */
public class SessionAlreadyStartedException extends SessionHandlingException {
    public SessionAlreadyStartedException(String message) {
        super(message);
    }
}

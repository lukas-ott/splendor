package de.spl12.domain.Exceptions.SessionHandlingExceptions;

/**
 * Indicates that the session is a single player session.
 */
public class SinglePlayerSessionException extends SessionHandlingException{
    public SinglePlayerSessionException(String message) {
        super(message);
    }
}

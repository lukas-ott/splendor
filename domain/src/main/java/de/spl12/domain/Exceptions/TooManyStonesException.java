package de.spl12.domain.Exceptions;

/**
 * Indicates that the player has more than 10 Stones when trying to end his turn.
 *
 * @author leon.kuersch
 */
public class TooManyStonesException extends ActionNotPossibleException {
    public TooManyStonesException(String message) {
        super(message);
    }
}

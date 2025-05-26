package de.spl12.domain.Exceptions;

/**
 * Indicates that the player tried to perform an action while it was not his turn.
 *
 * @author leon.kuersch
 */
public class NotYourTurnException extends ActionNotPossibleException {
    public NotYourTurnException() {
        super("Wait for your turn to perform this action");
    }
}

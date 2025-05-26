package de.spl12.domain.Exceptions;

/**
 * Indicates that the player tried to perform an action that is not possible after his previous action
 *
 * @author leon.kuersch
 */
public class IllegalMoveCombinationException extends ActionNotPossibleException{
    public IllegalMoveCombinationException(String message) {
        super(message);
    }
}

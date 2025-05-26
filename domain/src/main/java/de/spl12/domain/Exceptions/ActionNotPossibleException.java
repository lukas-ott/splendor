package de.spl12.domain.Exceptions;

/**
 * Indicates that the player tried to perform an action he is not allowed to.
 *
 * @author leon.kuersch
 */
public class ActionNotPossibleException extends Exception {
    public ActionNotPossibleException(String message) {
        super(message);
    }
}

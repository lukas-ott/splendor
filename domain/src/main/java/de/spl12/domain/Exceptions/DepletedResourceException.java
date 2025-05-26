package de.spl12.domain.Exceptions;


/**
 * Indicates that the player tried to use a resource that is not available.
 *
 * @author leon.kuersch
 */
public class DepletedResourceException extends ActionNotPossibleException {
    public DepletedResourceException(String message) {
        super(message);
    }
}

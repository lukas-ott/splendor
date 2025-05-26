package de.spl12.domain.Exceptions;

/**
 * Indicates that the player does not have the resources to buy the corresponding item.
 *
 * @author leon.kuersch
 */
public class CantAffordItemException extends ActionNotPossibleException {
    public CantAffordItemException(String message) {
        super(message);
    }
}

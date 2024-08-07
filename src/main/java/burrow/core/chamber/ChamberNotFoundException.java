package burrow.core.chamber;

import java.io.FileNotFoundException;

/**
 * Exception thrown when a specified chamber is not found.
 */
public final class ChamberNotFoundException extends FileNotFoundException {
    /**
     * Constructs a new ChamberNotFoundException with the specified chamber name.
     * @param chamberName The name of the chamber that could not be found.
     */
    public ChamberNotFoundException(final String chamberName) {
        super("Chamber not found: " + chamberName);
    }
}

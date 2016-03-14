package ca.josephroque.bowlingcompanion.utilities;

/**
 * Created by Joseph Roque on 2016-03-13. Provides methods and constants for enabling the transferring of data to a new
 * device.
 */
public final class TransferUtils {

    /** Time to wait before closing connection. */
    public static final int CONNECTION_TIMEOUT = 1000 * 10;
    /** Time to wait before closing connection, if previous connections failed. */
    public static final int CONNECTION_EXTENDED_TIMEOUT = 1000 * 25;

    /** Represents a timeout error. */
    public static final byte ERROR_TIMEOUT = 7;
    /** Represents an error in which a connection was cancelled. */
    public static final byte ERROR_CANCELLED = 6;
    /** Represents an IO error. */
    public static final byte ERROR_IO_EXCEPTION = 5;
    /** Represents an out of memory error during upload/download. */
    public static final byte ERROR_OUT_OF_MEMORY = 4;
    /** Represents an error in which the database file could not be found for upload. */
    public static final byte ERROR_FILE_NOT_FOUND = 3;
    /** Represents an incorrect URL error. */
    public static final byte ERROR_MALFORMED_URL = 2;
    /** Represents any other error which may occur during upload/download. */
    public static final byte ERROR_EXCEPTION = 1;
    /** Represents a successful upload/download. */
    public static final byte SUCCESS = 0;

    /**
     * Default private constructor.
     */
    private TransferUtils() {
        // does nothing
    }
}

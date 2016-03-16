package ca.josephroque.bowlingcompanion.utilities;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 2016-03-13. Provides methods and constants for enabling the transferring of data to a new
 * device.
 */
public final class TransferUtils {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TransferUtils";

    /** URL to upload or download data to/from. */
    private static String sTransferServerUrl = null;

    /** Target percentage of transfers. */
    public static final int TARGET_PERCENTAGE = 100;
    /** Starting location of the transfer key in the response. */
    public static final int TRANSFER_KEY_START = 10;
    /** Length of the transfer key in the response. */
    public static final int TRANSFER_KEY_LENGTH = 5;

    /** Time to wait before closing connection. */
    public static final int CONNECTION_TIMEOUT = 1000 * 10;
    /** Time to wait before closing connection, if previous connections failed. */
    public static final int CONNECTION_EXTENDED_TIMEOUT = 1000 * 25;

    /** Represents an invalid key provided by the user. */
    public static final String ERROR_INVALID_KEY = "INVALID_KEY";
    /** Represents an error in which the server is currently unavailable. User should try again later. */
    public static final String ERROR_UNAVAILABLE = "UNAVAILABLE";
    /** Represents a timeout error. */
    public static final String ERROR_TIMEOUT = "TIMEOUT";
    /** Represents an error in which a connection was cancelled. */
    public static final String ERROR_CANCELLED = "CANCELLED";
    /** Represents an IO error. */
    public static final String ERROR_IO_EXCEPTION = "IO";
    /** Represents an out of memory error during upload/download. */
    public static final String ERROR_OUT_OF_MEMORY = "OOM";
    /** Represents an error in which the database file could not be found for upload. */
    public static final String ERROR_FILE_NOT_FOUND = "MIA";
    /** Represents an incorrect URL error. */
    public static final String ERROR_MALFORMED_URL = "URL";
    /** Represents any other error which may occur during upload/download. */
    public static final String ERROR_EXCEPTION = "ERROR";
    /** Represents a successful download of bowler data. */
    public static final String SUCCESSFUL_IMPORT = "IM_SUCCESS";

    /** String modifier to represent location of downloaded data. */
    public static final String DATA_DOWNLOADED = "_dl";
    /** String modifier to represent location of backup data. */
    public static final String DATA_BACKUP = "_backup";

    /** Max buffer size during data transfer. */
    public static final int MAX_BUFFER_SIZE = 32 * 1024;
    /** Buffer size when backing up database. */
    public static final int BACKUP_BUFFER_SIZE = 1024;

    /**
     * Returns the URL for GET requests to check the status of the server.
     *
     * @return URL for server status check
     */
    public static String getStatusEndpoint() {
        return sTransferServerUrl + "status";
    }

    /**
     * Returns the URL for POST requests to upload bowler data.
     *
     * @return URL for data upload
     */
    public static String getUploadEndpoint() {
        return sTransferServerUrl + "upload";
    }

    /**
     * Returns the URL for GET requests to download user data.
     *
     * @param key unique key which represents data.
     * @return URL for data download.
     */
    public static String getDownloadEndpoint(String key) {
        return sTransferServerUrl + "download?key=" + key;
    }

    /**
     * Returns the URL for GET requests to confirm a key is valid.
     *
     * @param key unique key to check
     * @return URL for validity check
     */
    public static String getValidKeyEndpoint(String key) {
        return sTransferServerUrl + "valid?key=" + key;
    }

    /**
     * Loads the base URL for the transfer API.
     *
     * @param resources to access strings
     */
    public static void loadTransferServerUrl(Resources resources) {
        sTransferServerUrl = resources.getString(R.string.transfer_url);
    }

    /**
     * Performs a GET request to check if the provided key corresponds to valid data on the server.
     *
     * @param key unique key
     * @return {@code true} if the app should continue with downloading data with the key, {@code false} otherwise.
     */
    public static boolean isKeyValid(String key) {
        try {
            URL url = new URL(getValidKeyEndpoint(key));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder responseMsg = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    responseMsg.append(line);
                    line = reader.readLine();
                }
                reader.close();

                String response = responseMsg.toString().trim().toUpperCase();
                Log.d(TAG, "Transfer server status response: " + response);

                // The server is only ready to accept uploads if it responds with "VALID"
                return response.equals("VALID");
            } else {
                Log.e(TAG, "Invalid response getting server status: " + responseCode);
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Error parsing URL. This shouldn't happen.", ex);
        } catch (IOException ex) {
            Log.e(TAG, "Error opening or closing connection validating key.", ex);
        }

        return false;
    }

    /**
     * Performs a GET request to check the status of the server and if uploading or downloading data is possible. Should
     * NOT be run on the main thread.
     *
     * @return {@code true} if the app should continue with uploading/downloading data, {@code false} otherwise.
     */
    public static boolean getServerStatus() {
        try {
            URL url = new URL(getStatusEndpoint());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder responseMsg = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    responseMsg.append(line);
                    line = reader.readLine();
                }
                reader.close();

                String response = responseMsg.toString().trim().toUpperCase();
                Log.d(TAG, "Transfer server status response: " + response);

                // The server is only ready to accept uploads if it responds with "OK"
                return response.equals("OK");
            } else {
                Log.e(TAG, "Invalid response getting server status: " + responseCode);
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Error parsing URL. This shouldn't happen.", ex);
        } catch (IOException ex) {
            Log.e(TAG, "Error opening or closing connection getting status.", ex);
        }

        return false;
    }

    /**
     * Default private constructor.
     */
    private TransferUtils() {
        // does nothing
    }
}

package ca.josephroque.bowlingcompanion.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Joseph Roque on 2016-01-14. Offers utility methods for accessing raw files.
 */
public final class FileUtils {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "FileUtils";

    /**
     * Loads the text from the specified file in the assets.
     *
     * @param context to access assets
     * @param fileName name of the file in the assets
     * @return a String containing the text contained in {@code fileName}
     */
    public static String retrieveTextFileAsset(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();

        if (context == null || TextUtils.isEmpty(fileName))
            return null;

        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        } catch (IOException ex) {
            Log.e(TAG, "Could not read textfile: " + fileName, ex);
        }

        return stringBuilder.toString();
    }

    /**
     * Creates a copy of a file at a secondary location.
     *
     * @param fileToCopy file to back up
     * @param newLocation absolute path to create back up of file
     * @return {@code true} if the file was successfully copied, {@code false} otherwise
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean copyFile(File fileToCopy, String newLocation) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        File copy = new File(newLocation);
        copy.delete();

        try {
            inputStream = new FileInputStream(fileToCopy);
            outputStream = new FileOutputStream(copy);

            byte[] buffer = new byte[TransferUtils.BACKUP_BUFFER_SIZE];
            int dataLength = inputStream.read(buffer);
            while (dataLength > 0) {
                outputStream.write(buffer, 0, dataLength);
                dataLength = inputStream.read(buffer);
            }

            return true;
        } catch (IOException ex) {
            Log.e(TAG, "Failed to backup file.", ex);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ex) {
                Log.e(TAG, "Failed to close backup streams.", ex);
            }
        }

        return false;
    }

    /**
     * Default private constructors.
     */
    private FileUtils() {
        // does nothing
    }
}

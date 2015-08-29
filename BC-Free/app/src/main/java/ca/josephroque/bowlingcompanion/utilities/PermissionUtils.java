package ca.josephroque.bowlingcompanion.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 2015-08-29. Provides methods to prompt users for certain permissions.
 */
public final class PermissionUtils {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "PermissionUtils";

    /** Id for permission to write to external storage. */
    public static final int REQUEST_EXTERNAL_STORAGE = 0;

    /**
     * Prompts user for permission to write to external storage.
     *
     * @param activity current activity
     */
    public static void requestExternalStoragePermission(final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(activity)
                    .setMessage(R.string.permission_request_storage)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * Default private constructor.
     */
    private PermissionUtils() {
        // does nothing
    }
}

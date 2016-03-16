package ca.josephroque.bowlingcompanion.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import ca.josephroque.bowlingcompanion.Constants;

/**
 * Created by Joseph Roque on 2015-11-19. Provides utility methods for setting up the application when it is launched.
 */
public final class Startup {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Startup";

    /**
     * Handles startup events.
     *
     * @param context the current context
     */
    public static void onStartup(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ex) {
            // Exit method - cannot update
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String oldVersion = preferences.getString(Constants.PREF_VERSION, "0");
        String newVersion = packageInfo.versionName;

        Changelog.setShowChangelog(context, !oldVersion.equals(newVersion));

        TransferUtils.loadTransferServerUrl(context.getResources());
        AppRater.appLaunched(context);
        Changelog.appLaunched(context);
        updateAppVersion(preferences, newVersion);
    }

    /**
     * Updates the stored app version to a new version.
     *
     * @param preferences to store new version
     * @param newVersion value of new version build number
     */
    private static void updateAppVersion(SharedPreferences preferences, String newVersion) {
        preferences
                .edit()
                .putString(Constants.PREF_VERSION, newVersion)
                .apply();
    }

    /**
     * Default private constructor.
     */
    private Startup() {
        // does nothing
    }
}

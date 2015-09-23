package ca.josephroque.bowlingcompanion;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Joseph Roque on 2015-09-22.
 * Initializes properties of the application when the user starts it.
 */
public class BowlingCompanionApplication extends Application {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "BowlingCompanionApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(Constants.PREF_RATE_ME_SHOWN, false).apply();
    }
}

package ca.josephroque.bowlingcompanion;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Joseph Roque on 2015-09-22. Initializes properties of the application when the user starts it.
 */
public class BowlingCompanionApplication
        extends Application {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "BowlingCompanionApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        final int defaultScoreHighlight = 60;
        final int maxScoreHighlight = 90;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean convertScoreHighlight = false;
        String scoreHighlightStr;
        int scoreHighlightInt;
        try {
            scoreHighlightStr = preferences.getString(Constants.KEY_HIGHLIGHT_SCORE, "300");
            scoreHighlightInt = Integer.parseInt(scoreHighlightStr);
            if (scoreHighlightInt > maxScoreHighlight)
                convertScoreHighlight = true;
        } catch (Exception ex) {
            scoreHighlightInt = defaultScoreHighlight;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.PREF_RATE_ME_SHOWN, false);
        if (convertScoreHighlight) {
            editor.putString(Constants.KEY_HIGHLIGHT_SCORE,
                    Integer.toString(scoreHighlightInt / Constants.HIGHLIGHT_INCREMENT));
        }
        editor.apply();
    }
}

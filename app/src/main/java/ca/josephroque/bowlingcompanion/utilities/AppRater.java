package ca.josephroque.bowlingcompanion.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 15-03-03. Provides methods for determining the level of user interaction with the
 * application, and offering a prompt to rate the app if they desire, or disable the prompt if not.
 */
public final class AppRater {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "AppRater";

    /** Package the app has been created in. */
    private static final String APP_PACKAGE_NAME = "ca.josephroque.bowlingcompanion";

    /** Minimum number of days to wait before displaying prompt. */
    private static final int DAYS_UNTIL_PROMPT = 14;
    /** Minimum number of launches to wait before displaying prompt. */
    private static final int LAUNCHES_UNTIL_PROMPT = 3;
    /** Minimum number of days to wait between showings of the rate prompt. */
    private static final int DAYS_BETWEEN_PROMPTS = 14;

    /** Identifier for number of times app has been launched, stored in preferences. */
    private static final String PREF_LAUNCH_COUNT = "lc";
    /** Identifier for indicating whether the prompt should be shown or not. */
    private static final String PREF_DO_NOT_SHOW = "ds";
    /** Identifier for date of first launch, stored in preferences. */
    private static final String PREF_FIRST_LAUNCH = "fl";
    /** Identifier for date of the last time the app prompted the user to rate. */
    private static final String PREF_LAST_PROMPT_TIME = "apprate_last_prompt_time";

    /** Amount of time the app should wait before showing the rate dialog, in milliseconds. */
    private static final long MILLISECONDS_UNTIL_PROMPT = DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000;
    /** Amount of time the app should wait between showing the rate dialog, in milliseconds. */
    private static final long MILLISECONDS_BETWEEN_PROMPTS = DAYS_BETWEEN_PROMPTS * 24 * 60 * 60 * 1000;

    /**
     * Checks whether conditions to display the prompt have been met and, if so, displays it.
     *
     * @param context context to contain the prompt
     */
    public static void appLaunched(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(PREF_DO_NOT_SHOW, false))
            return;

        SharedPreferences.Editor editor = preferences.edit();

        // Gets number of launches and updates the count
        long launchCount = preferences.getLong(PREF_LAUNCH_COUNT, 0) + 1;
        editor.putLong(PREF_LAUNCH_COUNT, launchCount);

        // Gets the date of the first launch and updates it if not set
        Long dateOfFirstLaunch = preferences.getLong(PREF_FIRST_LAUNCH, 0);
        if (dateOfFirstLaunch == 0) {
            dateOfFirstLaunch = System.currentTimeMillis();
            editor.putLong(PREF_FIRST_LAUNCH, dateOfFirstLaunch);
        }


        // Gets the date to wait for, in milliseconds
        long dateToWaitFor = dateOfFirstLaunch + MILLISECONDS_UNTIL_PROMPT;

        // Checks if the user has been prompted before and updates the next time to prompt accordingly
        Long lastPromptTime = preferences.getLong(PREF_LAST_PROMPT_TIME, 0);
        if (lastPromptTime != 0) {
          dateToWaitFor = lastPromptTime + MILLISECONDS_BETWEEN_PROMPTS;
        }

        // If the conditions have been met, display the prompt
        if (launchCount >= LAUNCHES_UNTIL_PROMPT
                && System.currentTimeMillis() >= dateToWaitFor) {
            showRateDialog(context, editor);
            editor.putLong(PREF_LAST_PROMPT_TIME, System.currentTimeMillis());
        }

        editor.apply();
    }

    /**
     * Displays a prompt to the user to open the app store / browser to rate the app.
     *
     * @param context context to contain the prompt
     * @param editor preference editor to update preferences
     */
    private static void showRateDialog(final Context context, final SharedPreferences.Editor editor) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View rootView = View.inflate(context, R.layout.dialog_rate, null);

        dialog.setView(rootView);
        final AlertDialog alertDialog = dialog.create();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                switch (v.getId()) {
                    case R.id.btn_rate:
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + APP_PACKAGE_NAME)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PACKAGE_NAME)));
                        }
                        disableAutomaticPrompt(editor);
                        break;
                    case R.id.btn_rate_no:
                        disableAutomaticPrompt(editor);
                        break;
                    case R.id.btn_rate_remind_me:
                        break;
                    default:
                        // do nothing
                }
            }
        };

        rootView.findViewById(R.id.btn_rate).setOnClickListener(listener);
        rootView.findViewById(R.id.btn_rate_no).setOnClickListener(listener);
        rootView.findViewById(R.id.btn_rate_remind_me).setOnClickListener(listener);

        alertDialog.show();
    }

    /**
     * Disables the prompt from appearing again.
     *
     * @param editor preference editor to update preferences
     */
    private static void disableAutomaticPrompt(final SharedPreferences.Editor editor) {
        editor.putBoolean(PREF_DO_NOT_SHOW, true)
              .apply();
    }

    /**
     * Default private constructor.
     */
    private AppRater() {
        // does nothing
    }
}

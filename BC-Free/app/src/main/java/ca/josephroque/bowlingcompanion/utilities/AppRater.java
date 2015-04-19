package ca.josephroque.bowlingcompanion.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-03-03.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.external
 * in project Bowling Companion
 *
 * Retrieved from http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
 */
public class AppRater
{
    /** Name of the app */
    private static final String APP_NAME = "Bowling Companion";
    /** Package the app has been created in */
    private static final String APP_PNAME = "ca.josephroque.bowlingcompanion";

    /** Minimum number of days to wait before displaying prompt */
    private static final int DAYS_UNTIL_PROMPT = 14;
    /** Minimum number of launches to wait before displaying prompt */
    private static final int LAUNCHES_UNTIL_PROMPT = 3;

    /** Identifier for number of times app has been launched, stored in preferences */
    private static final String PREF_LAUNCH_COUNT = "lc";
    /** Identifier for indicating whether the prompt should be shown or not */
    private static final String PREF_DONT_SHOW = "ds";
    /** Identifier for date of first launch, stored in preferences */
    private static final String PREF_FIRST_LAUNCH = "fl";

    /**
     * Checks whether conditions to display the prompt have been met and, if so, displays it
     * @param context context to contain the prompt
     */
    public static void appLaunched(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        if (preferences.getBoolean(PREF_DONT_SHOW, false))
            return;

        SharedPreferences.Editor editor = preferences.edit();

        //Gets number of launches and updates the count
        long launchCount = preferences.getLong(PREF_LAUNCH_COUNT, 0) + 1;
        editor.putLong(PREF_LAUNCH_COUNT, launchCount);

        //Gets the date of the first launch and updates it if not set
        Long dateOfFirstLaunch = preferences.getLong(PREF_FIRST_LAUNCH, 0);
        if (dateOfFirstLaunch == 0)
        {
            dateOfFirstLaunch = System.currentTimeMillis();
            editor.putLong(PREF_FIRST_LAUNCH, dateOfFirstLaunch);
        }

        //Gets the dait to wait for, in milliseconds
        long dateToWaitFor = dateOfFirstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000);

        //If the conditions have been met, display the prompt
        if (launchCount >= LAUNCHES_UNTIL_PROMPT
                && System.currentTimeMillis() >= dateToWaitFor)
        {
            showRateDialog(context, editor);
        }

        editor.apply();
    }

    /**
     * Displays a prompt to the user to open the app store / browser to rate the app
     * @param context context to contain the prompt
     * @param editor preference editor to update preferences
     */
    public static void showRateDialog(final Context context, final SharedPreferences.Editor editor)
    {
        AlertDialog.Builder rateBuilder = new AlertDialog.Builder(context);
        rateBuilder.setTitle("If you like " + APP_NAME + ", please consider rating it. Thank you for your support!")
                .setSingleChoiceItems(new CharSequence[]{"Rate " + APP_NAME, "Remind me later", "No, thanks"}, 0, null)
                .setPositiveButton(R.string.dialog_add, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int pos = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        switch(pos)
                        {
                            case 0: //Rate
                                disableAutomaticPrompt(editor);

                                //Opens Google Play or browser to display app
                                try
                                {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                                } catch (android.content.ActivityNotFoundException ex)
                                {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PNAME)));
                                }
                                break;
                            case 2: //Disable
                                disableAutomaticPrompt(editor);
                                break;
                            case 1:default: // Remind me/other
                                //do nothing
                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    /**
     * Disables the prompt from appearing again
     * @param editor preference editor to update preferences
     */
    public static void disableAutomaticPrompt(final SharedPreferences.Editor editor)
    {
        editor.putBoolean(PREF_DONT_SHOW, true)
                .apply();
    }
}

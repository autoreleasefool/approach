package ca.josephroque.bowlingcompanion.external;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

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
    private static final String APP_NAME = "Bowling Companion";
    private static final String APP_PNAME = "ca.josephroque.bowlingcompanion";

    private static final int DAYS_UNTIL_PROMPT = 14;
    private static final int LAUNCHES_UNTIL_PROMPT = 3;

    private static final String PREF_RATING = "apprater";
    private static final String PREF_LAUNCH_COUNT = "lc";
    private static final String PREF_DONT_SHOW = "ds";
    private static final String PREF_FIRST_LAUNCH = "fl";

    public static void appLaunched(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(PREF_RATING, Context.MODE_PRIVATE);
        if (preferences.getBoolean(PREF_DONT_SHOW, false))
            return;

        SharedPreferences.Editor editor = preferences.edit();

        long launchCount = preferences.getLong(PREF_LAUNCH_COUNT, 0) + 1;
        editor.putLong(PREF_LAUNCH_COUNT, launchCount);

        Long dateOfFirstLaunch = preferences.getLong(PREF_FIRST_LAUNCH, 0);
        if (dateOfFirstLaunch == 0)
        {
            dateOfFirstLaunch = System.currentTimeMillis();
            editor.putLong(PREF_FIRST_LAUNCH, dateOfFirstLaunch);
        }

        long dateToWaitFor = dateOfFirstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000);
        if (launchCount >= LAUNCHES_UNTIL_PROMPT
                && System.currentTimeMillis() >= dateToWaitFor)
        {
            showRateDialog(context, editor);
        }

        editor.apply();
    }

    public static void showRateDialog(final Context context, final SharedPreferences.Editor editor)
    {
        AlertDialog.Builder rateBuilder = new AlertDialog.Builder(context);
        rateBuilder.setTitle("Rate " + APP_NAME)
                .setMessage("If you like " + APP_NAME + ", please consider rating it. Thank you for your support!")
                .setPositiveButton("Rate " + APP_NAME, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        disableAutomaticPrompt(editor);
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + APP_PNAME));
                        context.startActivity(rateIntent);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Remind me later", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        disableAutomaticPrompt(editor);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public static void disableAutomaticPrompt(final SharedPreferences.Editor editor)
    {
        editor.putBoolean(PREF_DONT_SHOW, true)
                .apply();
    }
}

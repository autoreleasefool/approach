package ca.josephroque.bowlingcompanion.utilities;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by josephroque on 15-03-24.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.utilities
 * in project Bowling Companion
 */
public class EmailUtils
{
    public static Intent getEmailIntent(
            String recipientEmail,
            String emailSubject,
            String emailBody)
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        return emailIntent;
    }

    public static Intent getEmailIntent(
            String recipientEmail,
            String emailSubject)
    {
        return getEmailIntent(recipientEmail, emailSubject, "");
    }
}

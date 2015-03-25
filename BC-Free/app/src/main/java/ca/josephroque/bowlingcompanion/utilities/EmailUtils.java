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

    /**
     * Creates an email intent and sets values to parameters
     * @param recipientEmail email recipient
     * @param emailSubject subject of the email
     * @param emailBody body of the email
     * @return new email intent
     */
    public static Intent getEmailIntent(
            String recipientEmail,
            String emailSubject,
            String emailBody)
    {
        if (emailBody == null)
            emailBody = "";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        return emailIntent;
    }

    /**
     * Creates an intent and sets values to parameters
     * @param recipientEmail email recipient
     * @param emailSubject subject of the email
     * @return getEmailIntent(recipientEmail, emailSubject, null)
     */
    public static Intent getEmailIntent(
            String recipientEmail,
            String emailSubject)
    {
        return getEmailIntent(recipientEmail, emailSubject, null);
    }
}

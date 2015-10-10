package ca.josephroque.bowlingcompanion.utilities;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by Joseph Roque on 15-03-24. Provides methods related to creating and formatting emails which the user can
 * select to create from the application. A local mail application on the device will be used to handle writing and
 * sending the email.
 */
public final class EmailUtils {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "EmailUtils";

    /**
     * Creates an email intent and sets values to parameters.
     *
     * @param recipientEmail email recipient
     * @param emailSubject subject of the email
     * @param emailBody body of the email
     * @return new email intent
     */
    public static Intent getEmailIntent(
            String recipientEmail,
            String emailSubject,
            String emailBody) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        if (recipientEmail != null)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        if (emailSubject != null)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        if (emailBody != null)
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        return emailIntent;
    }
    /**
     * Default private constructor.
     */
    private EmailUtils() {
        // does nothing
    }
}

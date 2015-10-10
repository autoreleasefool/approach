package ca.josephroque.bowlingcompanion.utilities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import ca.josephroque.bowlingcompanion.BuildConfig;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Joseph Roque on 2015-10-09. Tests the general utilities for creating and sending emails.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        packageName = "ca.josephroque.bowlingcompanion"
)
public class EmailUtilsTest {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "EmailUtilsTest";

    /** Represents a default recipient email address. */
    private static final String RECIPIENT_DEFAULT = "default@default.com";
    /** Represents a default email subject. */
    private static final String SUBJECT_DEFAULT = "Default Subject";
    /** Represents a default email body. */
    private static final String BODY_DEFAULT = "The body of an email";

    /**
     * Sets up the environment before each test runs.
     *
     * @throws Exception any exceptions which might be thrown
     */
    @Before
    public void setUp()
            throws Exception {
        // does nothing
    }

    /**
     * Cleans up the environment after each test runs.
     *
     * @throws Exception any exceptions which might be thrown
     */
    @After
    public void tearDown()
            throws Exception {
        // does nothing
    }

    /**
     * Provides null values to create an {@link android.content.Intent} to send an email, then checks to ensure the
     * {@link android.content.Intent} was instantiated with the appropriate values.
     */
    @Test
    public void testEmptyEmail() {
        Intent emailIntent = EmailUtils.getEmailIntent(null, null, null);
        Bundle extras = emailIntent.getExtras();
        assertNull("Recipient is not null.", extras.getStringArray(Intent.EXTRA_EMAIL));
        assertNull("Subject is not null.", extras.getString(Intent.EXTRA_SUBJECT));
        assertNull("Body is not null.", extras.getString(Intent.EXTRA_TEXT));
    }

    /**
     * Provides null or default values to create an {@link android.content.Intent} to send an email, then checks to
     * ensure the {@link android.content.Intent} was instantiated with the appropriate values.
     */
    @Test
    public void testEmailFields() {
        for (int i = 0; i < 2; i++) {
            String recipient = (i == 0)
                    ? null
                    : RECIPIENT_DEFAULT;
            for (int j = 0; j < 2; j++) {
                String subject = (j == 0)
                        ? null
                        : SUBJECT_DEFAULT;
                for (int k = 0; k < 2; k++) {
                    String body = (k == 0)
                            ? ""
                            : BODY_DEFAULT;
                    Intent emailIntent = EmailUtils.getEmailIntent(recipient, subject, body);
                    Bundle extras = emailIntent.getExtras();

                    assertThat(extras.getString(Intent.EXTRA_SUBJECT), is(subject));
                    assertThat(extras.getString(Intent.EXTRA_TEXT), is(body));
                    if (recipient != null)
                        assertThat(extras.getStringArray(Intent.EXTRA_EMAIL), is(new String[]{recipient}));
                    else
                        assertNull("Recipient is not null.", extras.getStringArray(Intent.EXTRA_EMAIL));
                }
            }
        }
    }
}

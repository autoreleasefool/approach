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

    @Test
    public void testEmptyEmail() {
        Intent emailIntent = EmailUtils.getEmailIntent(null, null, null);
        Bundle extras = emailIntent.getExtras();
        assertThat(new String[]{null}, is(extras.getStringArray(Intent.EXTRA_EMAIL)));
        assertThat(null, is(extras.getString(Intent.EXTRA_SUBJECT)));
        assertThat(null, is(extras.getString(Intent.EXTRA_EMAIL)));
    }
}

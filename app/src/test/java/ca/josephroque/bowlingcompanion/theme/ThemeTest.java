package ca.josephroque.bowlingcompanion.theme;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ca.josephroque.bowlingcompanion.BuildConfig;
import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Joseph Roque on 15-10-08. INSERT CLASS DESCRIPTION HERE
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        packageName = "ca.josephroque.bowlingcompanion"
)
public class ThemeTest {

    /**
     * Sets up the environment before each test runs.
     *
     * @throws Exception any exceptions which might be thrown
     */
    @Before
    public void setUp()
            throws Exception {

    }

    /**
     * Cleans up the environment after each test runs.
     *
     * @throws Exception any exceptions which might be thrown
     */
    @After
    public void tearDown()
            throws Exception {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .remove(Constants.KEY_THEME_COLORS)
                .apply();
    }

    /**
     * Tests the loading of the default theme.
     */
    @Test
    public void testLoadThemes() {
        Theme.loadTheme(RuntimeEnvironment.application);
        testThemeColorsValid(Theme.getThemeColors(RuntimeEnvironment.application.getResources(), "Blue"));
    }

    /**
     * Tests to make sure each theme applies the correct colors to the application.
     */
    @Test
    public void testSettingThemeColors() {
        String[] possibleThemes = RuntimeEnvironment.application.getResources()
                .getStringArray(R.array.pref_theme_colors_names);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);

        for (String theme : possibleThemes) {
            Theme.setTheme(RuntimeEnvironment.application, theme);
            testThemeColorsValid(Theme.getThemeColors(RuntimeEnvironment.application.getResources(), theme));
            String themeFromPreferences = preferences.getString(Constants.KEY_THEME_COLORS, "Blue");
            assertThat(themeFromPreferences, is(theme));
        }
    }

    /**
     * Checks to make sure that all of the colors of the theme have been applied correctly.
     *
     * @param expectedColors colors of theme which are expected
     */
    public void testThemeColorsValid(int[] expectedColors) {
        assertThat(Theme.getPrimaryThemeColor(), is(expectedColors[Theme.COLOR_PRIMARY]));
        assertThat(Theme.getSecondaryThemeColor(), is(expectedColors[Theme.COLOR_SECONDARY]));
        assertThat(Theme.getTertiaryThemeColor(), is(expectedColors[Theme.COLOR_TERTIARY]));
        assertThat(Theme.getHighlightThemeColor(), is(expectedColors[Theme.COLOR_HIGHLIGHT]));
        assertThat(Theme.getStatusThemeColor(), is(expectedColors[Theme.COLOR_STATUS]));
    }
}

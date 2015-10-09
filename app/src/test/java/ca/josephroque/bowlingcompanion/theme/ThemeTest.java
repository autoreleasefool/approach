package ca.josephroque.bowlingcompanion.theme;

import android.content.SharedPreferences;
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
@Config(constants = BuildConfig.class, sdk = 21, packageName = "ca.josephroque.bowlingcompanion")
public class ThemeTest {

    @Before
    public void setUp()
            throws Exception {

    }

    @After
    public void tearDown()
            throws Exception {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .remove(Constants.KEY_THEME_COLORS)
                .apply();
    }

    @Test
    public void testLoadThemes() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences.edit()
                .putString(Constants.KEY_THEME_COLORS, "Blue")
                .apply();

        Theme.loadTheme(RuntimeEnvironment.application);
        testThemeColorsValid(Theme.getThemeColors(RuntimeEnvironment.application.getResources(), "Blue"));
    }

    @Test
    public void testSettingThemeColors() {
        String[] possibleThemes = RuntimeEnvironment.application.getResources()
                .getStringArray(R.array.pref_theme_colors_names);

        for (String theme : possibleThemes) {
            Theme.setTheme(RuntimeEnvironment.application, theme);
            testThemeColorsValid(Theme.getThemeColors(RuntimeEnvironment.application.getResources(), theme));
        }
    }

    public void testThemeColorsValid(int[] expectedColors) {
        assertThat(Theme.getPrimaryThemeColor(), is(expectedColors[Theme.COLOR_PRIMARY]));
        assertThat(Theme.getSecondaryThemeColor(), is(expectedColors[Theme.COLOR_SECONDARY]));
        assertThat(Theme.getTertiaryThemeColor(), is(expectedColors[Theme.COLOR_TERTIARY]));
        assertThat(Theme.getHighlightThemeColor(), is(expectedColors[Theme.COLOR_HIGHLIGHT]));
        assertThat(Theme.getStatusThemeColor(), is(expectedColors[Theme.COLOR_STATUS]));
    }
}

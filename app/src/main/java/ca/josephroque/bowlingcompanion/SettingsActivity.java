package ca.josephroque.bowlingcompanion;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.Contract;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.HighlightsDialog;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.EmailUtils;
import ca.josephroque.bowlingcompanion.utilities.FacebookUtils;
import ca.josephroque.bowlingcompanion.utilities.LegalUtils;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices, settings are presented
 * as a single list. On tablets, settings are split by category, with category headers shown to the left of the list of
 * settings.
 */
public class SettingsActivity
        extends PreferenceActivity {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SettingsActivity";

    /** List of the current fragments in the activity. */
    private List<WeakReference<Fragment>> mActiveFragments = new ArrayList<>();

    /** Handles changes to preferences. */
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            List<BowlingPreferenceFragment> currentFragments
                    = getVisibleBowlingPreferenceFragments();

            for (BowlingPreferenceFragment fragment : currentFragments)
                fragment.onSharedPreferenceChanged(sharedPreferences, key);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        mActiveFragments.add(new WeakReference<>(fragment));
    }

    /**
     * Gets a list of {@link ca.josephroque.bowlingcompanion.SettingsActivity.BowlingPreferenceFragment} instances which
     * are currently visible in the activity.
     *
     * @return list of fragments derived from {@code mActiveFragments}
     */
    private List<BowlingPreferenceFragment> getVisibleBowlingPreferenceFragments() {
        List<BowlingPreferenceFragment> fragments = new ArrayList<>();
        for (Iterator<WeakReference<Fragment>> it = mActiveFragments.iterator(); it.hasNext();) {
            Fragment fragment = it.next().get();
            if (fragment != null) {
                if (fragment.isVisible() && fragment instanceof BowlingPreferenceFragment)
                    fragments.add((BowlingPreferenceFragment) fragment);
            } else {
                it.remove();
            }
        }
        return fragments;
    }

    /**
     * Gathers common functionality for fragments in this class.
     */
    public abstract static class BowlingPreferenceFragment
            extends PreferenceFragment {

        @Override
        public void onResume() {
            super.onResume();
            setupPreferenceSummaries();
        }

        /**
         * Loads summaries for the preferences in this fragment. Automatically invoked when fragment resumes.
         */
        abstract void setupPreferenceSummaries();

        /**
         * Invoked by parent activity when a preference is changed.
         *
         * @param sharedPreferences shared preferences of the app
         * @param key identifier for the changed preference
         */
        abstract void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key);
    }

    /**
     * This fragment shows theme preferences only. It is used when the activity is showing a two-pane settings UI.
     */
    public static class ThemePreferenceFragment
            extends BowlingPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_theme);
        }

        @Override
        void setupPreferenceSummaries() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String themeColor = preferences.getString(Constants.KEY_THEME_COLORS, "Blue");
            findPreference(Constants.KEY_THEME_COLORS).setSummary("Current theme is " + themeColor);

            int scoreHighlight = Integer.parseInt(preferences.getString(Constants.KEY_HIGHLIGHT_SCORE, "60"));
            findPreference(Constants.KEY_HIGHLIGHT_SCORE)
                    .setSummary(
                            "Scores over "
                                    + (scoreHighlight * Constants.HIGHLIGHT_INCREMENT)
                                    + " will be highlighted");

            int seriesHighlight = Integer.parseInt(preferences.getString(Constants.KEY_HIGHLIGHT_SERIES, "160"));
            findPreference(Constants.KEY_HIGHLIGHT_SERIES)
                    .setSummary(
                            "Series over "
                                    + (seriesHighlight * Constants.HIGHLIGHT_INCREMENT)
                                    + " will be highlighted");
        }

        @Override
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case Constants.KEY_THEME_COLORS:
                    String themeColor = sharedPreferences.getString(key, "Blue");
                    Preference themePref = findPreference(key);
                    themePref.setSummary("Current theme is " + themeColor);

                    Theme.setTheme(getActivity(), themeColor);
                    break;
                case Constants.KEY_HIGHLIGHT_SCORE:
                    HighlightsDialog scorePref = (HighlightsDialog) findPreference(key);
                    int scoreHighlight = scorePref.getValue();
                    scorePref.setSummary("Scores over "
                            + (scoreHighlight * Constants.HIGHLIGHT_INCREMENT)
                            + " will be highlighted");
                    break;
                case Constants.KEY_HIGHLIGHT_SERIES:
                    HighlightsDialog seriesPref = (HighlightsDialog) findPreference(key);
                    int seriesHighlight = seriesPref.getValue();
                    seriesPref.setSummary("Series over "
                            + (seriesHighlight * Constants.HIGHLIGHT_INCREMENT)
                            + " will be highlighted");
                    break;
                default:
                    // does nothing
            }
        }
    }

    /**
     * This fragment shows quick series preferences only. It is used when the activity is showing a two-pane settings
     * UI.
     */
    public static class QuickSeriesPreferenceFragment
            extends BowlingPreferenceFragment {

        /** Array of ids which represent bowlers in the database. */
        private String[] mArrayBowlerIds;
        /** Array of names from database of bowlers. */
        private String[] mArrayBowlerNames;
        /** Array of ids which represents leagues in the database. */
        private String[][] mArrayLeagueIds;
        /** Array of names from database of leagues. */
        private String[][] mArrayLeagueNames;

        /** Currently selected bowler in mArrayBowlerNames. */
        private int mCurrentBowlerPosition;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_quick);
        }

        @Override
        public void onResume() {
            loadBowlerAndLeagueNames();
            super.onResume();
        }

        @Override
        void setupPreferenceSummaries() {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

            Preference preference = findPreference(Constants.KEY_ENABLE_QUICK);
            boolean quickSeriesEnabled = preferences.getBoolean(Constants.KEY_ENABLE_QUICK, false);
            if (quickSeriesEnabled) {
                preference.setSummary(R.string.pref_enable_quick_summaryOn);
                ListPreference quickBowlerPref = (ListPreference) findPreference(Constants.KEY_QUICK_BOWLER);
                ListPreference quickLeaguePref = (ListPreference) findPreference(Constants.KEY_QUICK_LEAGUE);

                quickBowlerPref.setEntries(mArrayBowlerNames);
                quickBowlerPref.setEntryValues(mArrayBowlerIds);

                long quickBowlerId = preferences.getLong(Constants.PREF_QUICK_BOWLER_ID, -1);
                long quickLeagueId = preferences.getLong(Constants.PREF_QUICK_LEAGUE_ID, -1);

                mCurrentBowlerPosition = Arrays.binarySearch(mArrayBowlerIds, String.valueOf(quickBowlerId));
                if (mCurrentBowlerPosition < 0)
                    mCurrentBowlerPosition = 0;
                quickBowlerPref.setValueIndex(mCurrentBowlerPosition);
                quickBowlerPref.setSummary(mArrayBowlerNames[mCurrentBowlerPosition]);

                quickLeaguePref.setEntryValues(mArrayLeagueIds[mCurrentBowlerPosition]);
                quickLeaguePref.setEntries(mArrayLeagueNames[mCurrentBowlerPosition]);

                int position = Arrays.binarySearch(mArrayLeagueIds[mCurrentBowlerPosition],
                        String.valueOf(quickLeagueId));
                if (position < 0)
                    position = 0;
                quickLeaguePref.setValueIndex(position);
                quickLeaguePref.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][position]);
            } else {
                preference.setSummary(R.string.pref_enable_quick_summaryOff);
                findPreference(Constants.KEY_QUICK_BOWLER).setSummary(R.string.pref_quick_bowler_summary);
                findPreference(Constants.KEY_QUICK_LEAGUE).setSummary(R.string.pref_quick_league_summary);
            }
        }

        @Override
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case Constants.KEY_ENABLE_QUICK:
                    boolean isQuickEnabled = sharedPreferences.getBoolean(key, false);
                    Preference quickPref = findPreference(key);
                    enableQuickSeries(sharedPreferences, isQuickEnabled, quickPref);
                    break;
                case Constants.KEY_QUICK_BOWLER:
                    ListPreference bowlerPref = (ListPreference) findPreference(key);
                    ListPreference leaguePref = (ListPreference) findPreference(Constants.KEY_QUICK_LEAGUE);

                    String bowlerId = bowlerPref.getValue();
                    mCurrentBowlerPosition = Arrays.binarySearch(mArrayBowlerIds, bowlerId);
                    leaguePref.setEntryValues(mArrayLeagueIds[mCurrentBowlerPosition]);
                    leaguePref.setEntries(mArrayLeagueNames[mCurrentBowlerPosition]);
                    leaguePref.setValueIndex(0);
                    bowlerPref.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][0]);

                    sharedPreferences
                            .edit()
                            .putLong(Constants.PREF_QUICK_BOWLER_ID,
                                    Long.parseLong(mArrayBowlerIds[mCurrentBowlerPosition]))
                            .putLong(Constants.PREF_QUICK_LEAGUE_ID,
                                    Long.parseLong(mArrayLeagueIds[mCurrentBowlerPosition][0]))
                            .apply();
                    break;
                case Constants.KEY_QUICK_LEAGUE:
                    ListPreference quickLeaguePref = (ListPreference) findPreference(key);
                    String leagueId = quickLeaguePref.getValue();
                    int position = Arrays.binarySearch(mArrayLeagueIds[mCurrentBowlerPosition], leagueId);
                    quickLeaguePref.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][position]);

                    sharedPreferences
                            .edit()
                            .putLong(Constants.PREF_QUICK_LEAGUE_ID,
                                    Long.parseLong(mArrayLeagueIds[mCurrentBowlerPosition][position]))
                            .apply();
                    break;
                default:
                    // does nothing
            }
        }

        /**
         * Enables or disables creating a quick series.
         *
         * @param sharedPreferences shared preferences of the application
         * @param isQuickEnabled true to enable creating quick series, false to disable
         * @param quickPref preference for enabling or disabling quick series
         */
        private void enableQuickSeries(SharedPreferences sharedPreferences,
                                       boolean isQuickEnabled,
                                       Preference quickPref) {
            ListPreference bowlerListPref = (ListPreference) findPreference(Constants.KEY_QUICK_BOWLER);
            ListPreference leagueListPref = (ListPreference) findPreference(Constants.KEY_QUICK_LEAGUE);

            if (isQuickEnabled) {
                quickPref.setSummary(R.string.pref_enable_quick_summaryOn);
                bowlerListPref.setValueIndex(0);

                bowlerListPref.setSummary(mArrayBowlerNames[0]);
                leagueListPref.setSummary(mArrayLeagueNames[0][0]);
                leagueListPref.setEntries(mArrayLeagueNames[0]);
                leagueListPref.setEntryValues(mArrayLeagueIds[0]);

                sharedPreferences
                        .edit()
                        .putLong(Constants.PREF_QUICK_BOWLER_ID, Long.parseLong(mArrayBowlerIds[0]))
                        .putLong(Constants.PREF_QUICK_LEAGUE_ID, Long.parseLong(mArrayLeagueIds[0][0]))
                        .apply();
            } else {
                quickPref.setSummary(R.string.pref_enable_quick_summaryOff);
                bowlerListPref.setSummary(R.string.pref_quick_bowler_summary);
                leagueListPref.setSummary(R.string.pref_quick_league_summary);
                sharedPreferences
                        .edit()
                        .putLong(Constants.PREF_QUICK_BOWLER_ID, -1)
                        .putLong(Constants.PREF_QUICK_LEAGUE_ID, -1)
                        .apply();
            }
        }

        /**
         * Loads bowler and league names from the database for user to select from when choosing 'quick'
         * bowlers/leagues.
         */
        private void loadBowlerAndLeagueNames() {
            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();
            String rawNameQuery = "SELECT "
                    + "bowler." + Contract.BowlerEntry._ID + " AS bid, "
                    + Contract.BowlerEntry.COLUMN_BOWLER_NAME + ", "
                    + "league." + Contract.LeagueEntry._ID + " AS lid, "
                    + Contract.LeagueEntry.COLUMN_LEAGUE_NAME
                    + " FROM " + Contract.BowlerEntry.TABLE_NAME + " AS bowler"
                    + " INNER JOIN " + Contract.LeagueEntry.TABLE_NAME + " AS league"
                    + " ON bowler." + Contract.BowlerEntry._ID + "=league." + Contract.LeagueEntry.COLUMN_BOWLER_ID
                    + " WHERE " + Contract.LeagueEntry.COLUMN_IS_EVENT + "=? AND "
                    + Contract.LeagueEntry.COLUMN_LEAGUE_NAME + " !=?"
                    + " ORDER BY " + Contract.BowlerEntry.COLUMN_BOWLER_NAME + ", "
                    + Contract.LeagueEntry.COLUMN_LEAGUE_NAME;
            String[] rawNameArgs = {"0", Constants.NAME_OPEN_LEAGUE};

            List<String> listBowlerNames = new ArrayList<>();
            List<String> listBowlerIds = new ArrayList<>();
            List<List<String>> listLeagueNames = new ArrayList<>();
            List<List<String>> listLeagueIds = new ArrayList<>();

            long lastBowlerId = -1;
            int currentLeaguePosition = -1;
            Cursor cursor = database.rawQuery(rawNameQuery, rawNameArgs);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long bowlerId = cursor.getLong(cursor.getColumnIndex("bid"));
                    if (lastBowlerId != bowlerId) {
                        lastBowlerId = bowlerId;
                        listBowlerNames.add(cursor.getString(cursor.getColumnIndex(
                                Contract.BowlerEntry.COLUMN_BOWLER_NAME)));
                        listBowlerIds.add(String.valueOf(bowlerId));
                        listLeagueIds.add(new ArrayList<String>());
                        listLeagueNames.add(new ArrayList<String>());
                        currentLeaguePosition++;
                    }
                    listLeagueIds.get(currentLeaguePosition)
                            .add(String.valueOf(cursor.getLong(cursor.getColumnIndex("lid"))));
                    listLeagueNames.get(currentLeaguePosition)
                            .add(cursor.getString(cursor.getColumnIndex(
                                    Contract.LeagueEntry.COLUMN_LEAGUE_NAME)));

                    cursor.moveToNext();
                }
            }
            cursor.close();

            //Gets arrays from lists of names & ids created above
            mArrayBowlerIds = new String[listBowlerIds.size()];
            listBowlerIds.toArray(mArrayBowlerIds);
            mArrayBowlerNames = new String[listBowlerNames.size()];
            listBowlerNames.toArray(mArrayBowlerNames);

            mArrayLeagueIds = new String[listLeagueIds.size()][];
            mArrayLeagueNames = new String[listLeagueNames.size()][];
            for (int i = 0; i < mArrayLeagueIds.length; i++) {
                mArrayLeagueIds[i] = new String[listLeagueIds.get(i).size()];
                listLeagueIds.get(i).toArray(mArrayLeagueIds[i]);
                mArrayLeagueNames[i] = new String[listLeagueNames.get(i).size()];
                listLeagueNames.get(i).toArray(mArrayLeagueNames[i]);
            }
            prepareValidBowlers(listBowlerNames);
        }

        /**
         * Enables/disables options depending on if there are valid bowlers & leagues to select.
         *
         * @param listBowlerNames list of valid bowler names
         */
        private void prepareValidBowlers(List<String> listBowlerNames) {
            if (listBowlerNames.size() > 0) {
                findPreference(Constants.KEY_ENABLE_QUICK).setEnabled(true);
                ListPreference listPreference = (ListPreference) findPreference(Constants.KEY_QUICK_BOWLER);
                listPreference.setEntryValues(mArrayBowlerIds);
                listPreference.setEntries(mArrayBowlerNames);
            } else {
                CheckBoxPreference quickCheckBox = (CheckBoxPreference) findPreference(Constants.KEY_ENABLE_QUICK);
                quickCheckBox.setChecked(false);
                quickCheckBox.setEnabled(false);
                findPreference(Constants.KEY_QUICK_BOWLER).setSummary(R.string.pref_quick_bowler_summary);
                findPreference(Constants.KEY_QUICK_LEAGUE).setSummary(R.string.pref_quick_league_summary);
            }
        }
    }

    /**
     * This fragment shows league preferences only. It is used when the activity is showing a two-pane settings UI.
     */
    public static class LeaguePreferenceFragment
            extends BowlingPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_league);
        }

        @Override
        void setupPreferenceSummaries() {
            // does nothing
        }

        @Override
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }

    /**
     * This fragment shows game preferences only. It is used when the activity is showing a two-pane settings UI.
     */
    public static class GamePreferenceFragment
            extends BowlingPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_game);
        }

        @Override
        void setupPreferenceSummaries() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            boolean autoAdvanceEnabled = preferences.getBoolean(Constants.KEY_ENABLE_AUTO_ADVANCE, false);
            String autoAdvanceInterval = preferences.getString(Constants.KEY_AUTO_ADVANCE_TIME, "15 seconds");
            if (autoAdvanceEnabled)
                findPreference(Constants.KEY_AUTO_ADVANCE_TIME).setSummary(
                        "Frame will auto advance after " + autoAdvanceInterval + ".");
            else
                findPreference(Constants.KEY_AUTO_ADVANCE_TIME).setSummary(R.string.pref_auto_advance_time_summary);
        }

        @Override
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case Constants.KEY_AUTO_ADVANCE_TIME:
                    Preference autoAdvancePref = findPreference(key);
                    autoAdvancePref.setSummary("Frame will auto advance after "
                            + sharedPreferences.getString(key, "15 seconds"));
                    break;
                default:
                    // does nothing
            }
        }
    }

    /**
     * This fragment shows statistic preferences only. It is used when the activity is showing a two-pane settings UI.
     */
    public static class StatisticPreferenceFragment
            extends BowlingPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_stats);
        }

        @Override
        void setupPreferenceSummaries() {
            // does nothing
        }

        @Override
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // does nothing
        }
    }

    /**
     * This fragment shows other preferences only. It is used when the activity is showing a two-pane settings UI.
     */
    public static class OtherPreferenceFragment
            extends BowlingPreferenceFragment {

        /**
         * Handles click events on certain preferences.
         */
        private Preference.OnPreferenceClickListener mOnClickListener = new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (preference.getKey().equals(Constants.KEY_FACEBOOK_PAGE)) {
                    // Opens facebook app or chrome to display facebook page
                    startActivity(FacebookUtils.newFacebookIntent(getActivity().getPackageManager()));
                } else if (preference.getKey().equals(Constants.KEY_RATE)) {
                    //Opens Google Play or chrome to display app
                    final String appPackageName = getActivity().getPackageName();
                    Intent marketIntent;
                    try {
                        marketIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName));
                    } catch (android.content.ActivityNotFoundException ex) {
                        marketIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="
                                        + appPackageName));
                    }
                    startActivity(marketIntent);
                    return true;
                } else if (preference.getKey().equals(Constants.KEY_REPORT_BUG)) {
                    String emailBody =
                            "Please try to include as much of the following information as possible:"
                                    + "\nWhere in the application the bug occurred,"
                                    + "\nWhat you were doing when the bug occurred,"
                                    + "\nThe nature of the bug - fatal, minor, cosmetic (the way the app"
                                    + " looks)"
                                    + "\n\n";

                    Intent emailIntent = EmailUtils.getEmailIntent(
                            "bugs@josephroque.ca",
                            "Bug: Bowling Companion",
                            emailBody);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    return true;
                } else if (preference.getKey().equals(Constants.KEY_COMMENT_SUGGESTION)) {
                    Intent emailIntent = EmailUtils.getEmailIntent(
                            "contact@josephroque.ca",
                            "Comm/Sug: Bowling Companion",
                            null);
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    return true;
                } else if (preference.getKey().equals(Constants.KEY_ATTRIBUTION)) {
                    LegalUtils.displayAttributions(getActivity());
                }
                return false;
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_other);

            findPreference(Constants.KEY_FACEBOOK_PAGE).setOnPreferenceClickListener(mOnClickListener);
            findPreference(Constants.KEY_RATE).setOnPreferenceClickListener(mOnClickListener);
            findPreference(Constants.KEY_REPORT_BUG).setOnPreferenceClickListener(mOnClickListener);
            findPreference(Constants.KEY_COMMENT_SUGGESTION).setOnPreferenceClickListener(mOnClickListener);
            findPreference(Constants.KEY_ATTRIBUTION).setOnPreferenceClickListener(mOnClickListener);
        }

        @Override
        void setupPreferenceSummaries() {
            // does nothing
        }

        @Override
        void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // does nothing
        }
    }

    @Override
    public boolean isValidFragment(String fragmentName) {
        return ThemePreferenceFragment.class.getName().equals(fragmentName)
                || QuickSeriesPreferenceFragment.class.getName().equals(fragmentName)
                || LeaguePreferenceFragment.class.getName().equals(fragmentName)
                || GamePreferenceFragment.class.getName().equals(fragmentName)
                || StatisticPreferenceFragment.class.getName().equals(fragmentName)
                || OtherPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For example, 10" tablets are extra-large.
     *
     * @param context the current context
     * @return true if the screen is extra large
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }
}

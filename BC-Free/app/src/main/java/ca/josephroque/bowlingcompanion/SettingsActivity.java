package ca.josephroque.bowlingcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.EmailUtils;
import ca.josephroque.bowlingcompanion.utilities.FacebookUtils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices, settings are presented
 * as a single list. On tablets, settings are split by category, with category headers shown to the left of the list of
 * settings.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SettingsActivity";

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
    public void onResume() {
        super.onResume();
        //Register this object as a listener for preference changes
        getPreferenceManager()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        loadBowlerAndLeagueNames();
        setPreferenceSummaries();
    }

    @Override
    public void onPause() {
        //Unregisters this object as a listener for preference changes
        getPreferenceManager()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the device configuration dictates that a
     * simplified, single-pane UI should be shown.
     */
    private void setupSimplePreferencesScreen() {

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.
        addPreferencesFromResource(R.xml.pref_theme);

        // Add 'quick' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_quick);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_quick);

        //Add 'league' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_league);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_league);

        //Add 'game' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_game);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_game);

        // Add 'stats' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_stats);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_stats);

        // Add 'other' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_other);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_other);

        findPreference(Constants.KEY_FACEBOOK_PAGE).setOnPreferenceClickListener(this);
        findPreference(Constants.KEY_RATE).setOnPreferenceClickListener(this);
        findPreference(Constants.KEY_REPORT_BUG).setOnPreferenceClickListener(this);
        findPreference(Constants.KEY_COMMENT_SUGGESTION).setOnPreferenceClickListener(this);
    }

    /**
     * Loads bowler and league names from the database for user to select from when choosing 'quick' bowlers/leagues.
     */
    private void loadBowlerAndLeagueNames() {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
        String rawNameQuery = "SELECT "
                + "bowler." + BowlerEntry._ID + " AS bid, "
                + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                + "league." + LeagueEntry._ID + " AS lid, "
                + LeagueEntry.COLUMN_LEAGUE_NAME
                + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                + " INNER JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_BOWLER_ID
                + " WHERE " + LeagueEntry.COLUMN_IS_EVENT + "=? AND "
                + LeagueEntry.COLUMN_LEAGUE_NAME + " !=?"
                + " ORDER BY " + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                + LeagueEntry.COLUMN_LEAGUE_NAME;
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
                            BowlerEntry.COLUMN_BOWLER_NAME)));
                    listBowlerIds.add(String.valueOf(bowlerId));
                    listLeagueIds.add(new ArrayList<String>());
                    listLeagueNames.add(new ArrayList<String>());
                    currentLeaguePosition++;
                }
                listLeagueIds.get(currentLeaguePosition)
                        .add(String.valueOf(cursor.getLong(cursor.getColumnIndex("lid"))));
                listLeagueNames.get(currentLeaguePosition)
                        .add(cursor.getString(cursor.getColumnIndex(
                                LeagueEntry.COLUMN_LEAGUE_NAME)));

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

        //Enables/disables options depending on if there are valid bowlers & leagues to select
        if (listBowlerNames.size() > 0) {
            findPreference(Constants.KEY_ENABLE_QUICK).setEnabled(true);
            ListPreference listPreference =
                    (ListPreference) findPreference(Constants.KEY_QUICK_BOWLER);
            listPreference.setEntryValues(mArrayBowlerIds);
            listPreference.setEntries(mArrayBowlerNames);
        } else {
            CheckBoxPreference checkBoxPreference =
                    (CheckBoxPreference) findPreference(Constants.KEY_ENABLE_QUICK);
            checkBoxPreference.setChecked(false);
            checkBoxPreference.setEnabled(false);
            findPreference(Constants.KEY_QUICK_BOWLER)
                    .setSummary(R.string.pref_quick_bowler_summary);
            findPreference(Constants.KEY_QUICK_LEAGUE)
                    .setSummary(R.string.pref_quick_league_summary);
        }
    }

    /**
     * Sets the summaries of preferences to their starting values.
     */
    private void setPreferenceSummaries() {
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        Preference preference = findPreference(Constants.KEY_ENABLE_QUICK);
        boolean checkBoolean = sharedPreferences.getBoolean(Constants.KEY_ENABLE_QUICK, false);
        if (checkBoolean) {
            preference.setSummary(R.string.pref_enable_quick_summaryOn);
            ListPreference quickBowlerPref =
                    (ListPreference) findPreference(Constants.KEY_QUICK_BOWLER);
            ListPreference quickLeaguePref =
                    (ListPreference) findPreference(Constants.KEY_QUICK_LEAGUE);

            quickBowlerPref.setEntries(mArrayBowlerNames);
            quickBowlerPref.setEntryValues(mArrayBowlerIds);

            SharedPreferences preferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
            long quickBowlerId = preferences.getLong(Constants.PREF_QUICK_BOWLER_ID, -1);
            long quickLeagueId = preferences.getLong(Constants.PREF_QUICK_LEAGUE_ID, -1);

            mCurrentBowlerPosition =
                    Arrays.binarySearch(mArrayBowlerIds, String.valueOf(quickBowlerId));
            if (mCurrentBowlerPosition < 0)
                mCurrentBowlerPosition = 0;
            quickBowlerPref.setValueIndex(mCurrentBowlerPosition);
            quickBowlerPref.setSummary(mArrayBowlerNames[mCurrentBowlerPosition]);

            quickLeaguePref.setEntryValues(mArrayLeagueIds[mCurrentBowlerPosition]);
            quickLeaguePref.setEntries(mArrayLeagueNames[mCurrentBowlerPosition]);

            int position =
                    Arrays.binarySearch(mArrayLeagueIds[mCurrentBowlerPosition],
                            String.valueOf(quickLeagueId));
            if (position < 0)
                position = 0;
            quickLeaguePref.setValueIndex(position);
            quickLeaguePref.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][position]);
        } else {
            preference.setSummary(R.string.pref_enable_quick_summaryOff);
            findPreference(Constants.KEY_QUICK_BOWLER)
                    .setSummary(R.string.pref_quick_bowler_summary);
            findPreference(Constants.KEY_QUICK_LEAGUE)
                    .setSummary(R.string.pref_quick_league_summary);
        }

        String themeColor = sharedPreferences.getString(Constants.KEY_THEME_COLORS, "Blue");
        findPreference(Constants.KEY_THEME_COLORS).setSummary("Current theme is " + themeColor);

        String autoAdvanceInterval =
                sharedPreferences.getString(Constants.KEY_AUTO_ADVANCE_TIME, "15 seconds");
        if (checkBoolean)
            findPreference(Constants.KEY_AUTO_ADVANCE_TIME)
                    .setSummary("Frame will auto advance after " + autoAdvanceInterval);
        else
            findPreference(Constants.KEY_AUTO_ADVANCE_TIME)
                    .setSummary(R.string.pref_auto_advance_time_summary);

        String scoreHighlight = sharedPreferences.getString(Constants.KEY_HIGHLIGHT_SCORE, "300");
        findPreference(Constants.KEY_HIGHLIGHT_SCORE)
                .setSummary("Scores over " + scoreHighlight + " will be highlighted");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Updates summaries of preferences when their values are changed

        switch (key) {
            case Constants.KEY_ENABLE_QUICK:
                boolean isQuickEnabled = sharedPreferences.getBoolean(key, false);
                Preference quickPref = findPreference(key);
                ListPreference bowlerListPref =
                        (ListPreference) findPreference(Constants.KEY_QUICK_BOWLER);
                ListPreference leagueListPref =
                        (ListPreference) findPreference(Constants.KEY_QUICK_LEAGUE);

                if (isQuickEnabled) {
                    quickPref.setSummary(R.string.pref_enable_quick_summaryOn);
                    bowlerListPref.setValueIndex(0);

                    bowlerListPref.setSummary(mArrayBowlerNames[0]);
                    leagueListPref.setSummary(mArrayLeagueNames[0][0]);
                    leagueListPref.setEntries(mArrayLeagueNames[0]);
                    leagueListPref.setEntryValues(mArrayLeagueIds[0]);

                    getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
                            .edit()
                            .putLong(Constants.PREF_QUICK_BOWLER_ID,
                                    Long.parseLong(mArrayBowlerIds[0]))
                            .putLong(Constants.PREF_QUICK_LEAGUE_ID,
                                    Long.parseLong(mArrayLeagueIds[0][0]))
                            .apply();
                } else {
                    quickPref.setSummary(R.string.pref_enable_quick_summaryOff);
                    bowlerListPref.setSummary(R.string.pref_quick_bowler_summary);
                    leagueListPref.setSummary(R.string.pref_quick_league_summary);
                    getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
                            .edit()
                            .putLong(Constants.PREF_QUICK_BOWLER_ID, -1)
                            .putLong(Constants.PREF_QUICK_LEAGUE_ID, -1)
                            .apply();
                }
                break;
            case Constants.KEY_QUICK_BOWLER:
                ListPreference bowlerPref =
                        (ListPreference) findPreference(key);
                ListPreference leaguePref =
                        (ListPreference) findPreference(Constants.KEY_QUICK_LEAGUE);

                String bowlerId = bowlerPref.getValue();
                mCurrentBowlerPosition = Arrays.binarySearch(mArrayBowlerIds, bowlerId);
                leaguePref.setEntryValues(mArrayLeagueIds[mCurrentBowlerPosition]);
                leaguePref.setEntries(mArrayLeagueNames[mCurrentBowlerPosition]);
                leaguePref.setValueIndex(0);
                bowlerPref.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][0]);

                getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
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
                int position = Arrays.binarySearch(mArrayLeagueIds[mCurrentBowlerPosition],
                        leagueId);
                quickLeaguePref.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][position]);

                getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
                        .edit()
                        .putLong(Constants.PREF_QUICK_LEAGUE_ID,
                                Long.parseLong(mArrayLeagueIds[mCurrentBowlerPosition][position]))
                        .apply();
                break;
            case Constants.KEY_THEME_COLORS:
                String themeColor =
                        sharedPreferences.getString(key, "Blue");

                Preference themePref = findPreference(key);
                themePref.setSummary("Current theme is " + themeColor);

                Theme.setTheme(this, themeColor);
                break;
            case Constants.KEY_HIGHLIGHT_SCORE:
                ListPreference highlightPref = (ListPreference) findPreference(key);
                highlightPref.setSummary("Scores over " + highlightPref.getValue()
                        + " will be highlighted");
                break;
            case Constants.KEY_AUTO_ADVANCE_TIME:
                Preference autoAdvancePref = findPreference(key);
                autoAdvancePref.setSummary("Frame will auto advance after "
                        + sharedPreferences.getString(key, "15 seconds"));
                break;
            default:
                // does nothing
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(Constants.KEY_FACEBOOK_PAGE)) {
            // Opens facebook app or chrome to display facebook page
            startActivity(FacebookUtils.newFacebookIntent(getPackageManager()));
        } else if (preference.getKey().equals(Constants.KEY_RATE)) {
            //Opens Google Play or chrome to display app
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="
                                + appPackageName)));
            }
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
                    "Comm/Sug: Bowling Companion");
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            return true;
        }
        return false;
    }

    @Override
    public boolean onIsMultiPane() {
        return false;
    }
}

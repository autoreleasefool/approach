package ca.josephroque.bowlingcompanion.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.ChangeableTheme;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-02-27.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragments
 * in project Bowling Companion
 */
public class SettingsFragment extends PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
{

    private static final String TAG = "SettingsFragment";

    private String[] mArrayBowlerNames;
    private String[][] mArrayLeagueNames;

    private String[] mArrayBowlerIdsAsStrings;
    private String[][] mArrayLeagueIdsAsStrings;

    private int mCurrentBowlerPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        loadBowlerAndLeagueNames();
        setPreferenceSummaries();
    }

    @Override
    public void onPause()
    {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals(Constants.KEY_PREF_ENABLE_QUICK))
        {
            boolean isQuickEnabled = sharedPreferences.getBoolean(key, false);
            Preference quickPref = findPreference(key);
            ListPreference quickBowlerPref = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_BOWLER);
            ListPreference quickLeaguePref = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_LEAGUE);

            if (isQuickEnabled)
            {
                quickPref.setSummary(R.string.pref_enable_quick_summary_quick);

                quickBowlerPref.setEnabled(true);
                quickLeaguePref.setEnabled(true);
                quickBowlerPref.setValueIndex(0);
                quickBowlerPref.setSummary(mArrayBowlerNames[0]);
                quickLeaguePref.setSummary(mArrayLeagueNames[0][0]);

                getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                        .edit()
                        .putLong(Constants.PREFERENCE_ID_QUICK_BOWLER, Long.parseLong(mArrayBowlerIdsAsStrings[0]))
                        .putLong(Constants.PREFERENCE_ID_QUICK_LEAGUE, Long.parseLong(mArrayLeagueIdsAsStrings[0][0]))
                        .apply();
            }
            else
            {
                quickPref.setSummary(R.string.pref_enable_quick_summary_recent);
                quickBowlerPref.setEnabled(false);
                quickBowlerPref.setSummary("");
                quickBowlerPref.setValueIndex(0);
                quickLeaguePref.setEnabled(false);
                quickLeaguePref.setSummary("");

                getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                        .edit()
                        .putLong(Constants.PREFERENCE_ID_QUICK_BOWLER, -1)
                        .putLong(Constants.PREFERENCE_ID_QUICK_LEAGUE, -1)
                        .apply();
            }
        }
        else if (key.equals(Constants.KEY_PREF_QUICK_BOWLER))
        {
            ListPreference quickBowlerPref = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_BOWLER);
            ListPreference quickLeaguePref = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_LEAGUE);

            String selectedBowlerId = quickBowlerPref.getValue();
            mCurrentBowlerPosition = Arrays.binarySearch(mArrayBowlerIdsAsStrings, selectedBowlerId);
            quickLeaguePref.setValueIndex(0);
            quickBowlerPref.setSummary(mArrayBowlerNames[mCurrentBowlerPosition]);

            getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(
                            Constants.PREFERENCE_ID_QUICK_BOWLER,
                            Long.parseLong(mArrayBowlerIdsAsStrings[mCurrentBowlerPosition]))
                    .apply();
        }
        else if (key.equals(Constants.KEY_PREF_QUICK_LEAGUE))
        {
            ListPreference quickLeaguePref = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_LEAGUE);

            String selectedLeagueId = quickLeaguePref.getValue();
            int selectedPosition = Arrays.binarySearch(mArrayLeagueIdsAsStrings[mCurrentBowlerPosition], selectedLeagueId);
            quickLeaguePref.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][selectedPosition]);

            getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(Constants.PREFERENCE_ID_QUICK_LEAGUE,
                            Long.parseLong(mArrayLeagueIdsAsStrings[mCurrentBowlerPosition][selectedPosition]))
                    .apply();
        }
        /*
        Removed disabling pin images - may add back later
        else if (key.equals(Constants.KEY_PREF_ENABLE_PINS))
        {
            boolean arePinsEnabled = sharedPreferences.getBoolean(key, true);
            Preference pinPref = findPreference(key);
            pinPref.setSummary((arePinsEnabled)
                    ? R.string.pref_enable_pins_summary_images
                    : R.string.pref_enable_pins_summary_buttons);
        }*/
        else if (key.equals(Constants.KEY_PREF_THEME_COLORS))
        {
            String themeColor = sharedPreferences.getString(key, "Green");
            boolean lightThemeEnabled = sharedPreferences.getBoolean(Constants.KEY_PREF_THEME_LIGHT, true);

            Preference themePref = findPreference(key);
            themePref.setSummary("Current theme is " + themeColor);

            Theme.setTheme(getActivity(), themeColor, lightThemeEnabled);
            ChangeableTheme themedActivity = (ChangeableTheme)getActivity();
            themedActivity.updateTheme();
        }
        else if (key.equals(Constants.KEY_PREF_THEME_LIGHT))
        {
            boolean lightThemeEnabled = sharedPreferences.getBoolean(key, true);
            Preference lightPref = findPreference(key);
            lightPref.setSummary((lightThemeEnabled)
                    ? R.string.pref_theme_light_summary
                    : R.string.pref_theme_dark_summary);

            Theme.setTheme(getActivity(), null, lightThemeEnabled);
            ChangeableTheme themedActivity = (ChangeableTheme)getActivity();
            themedActivity.updateTheme();
        }
    }

    private void loadBowlerAndLeagueNames()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

        String rawNameQuery = "SELECT "
                + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                + "bowler." + BowlerEntry._ID + " AS bid, "
                + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                + "league." + LeagueEntry._ID + " AS lid"
                + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                + " JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_NAME_BOWLER_ID
                + " WHERE " + LeagueEntry.COLUMN_NAME_IS_EVENT + "=? AND " + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + "!=?"
                + " ORDER BY " + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", " + LeagueEntry.COLUMN_NAME_LEAGUE_NAME;
        String[] rawNameArgs = {"0", Constants.NAME_LEAGUE_OPEN};
        Cursor cursor = database.rawQuery(rawNameQuery, rawNameArgs);

        List<String> listBowlerNames = new ArrayList<>();
        List<String> listBowlerIds = new ArrayList<>();
        List<List<String>> listLeagueNames = new ArrayList<>();
        List<List<String>> listLeagueIds = new ArrayList<>();

        long lastBowlerId = -1;
        int currentLeaguePosition = -1;
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                long bowlerId = cursor.getLong(cursor.getColumnIndex("bid"));

                if (lastBowlerId == bowlerId)
                {
                    listLeagueIds.get(currentLeaguePosition).add(
                            String.valueOf(cursor.getLong(cursor.getColumnIndex("lid"))));
                    listLeagueNames.get(currentLeaguePosition).add(
                            cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME)));
                }
                else
                {
                    lastBowlerId = bowlerId;
                    listBowlerNames.add(
                            cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME)));
                    listBowlerIds.add(String.valueOf(bowlerId));
                    listLeagueIds.add(new ArrayList<String>());
                    listLeagueNames.add(new ArrayList<String>());
                    currentLeaguePosition++;

                    listLeagueIds.get(currentLeaguePosition).add(
                            String.valueOf(cursor.getLong(cursor.getColumnIndex("lid"))));
                    listLeagueNames.get(currentLeaguePosition).add(
                            cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME)));
                }
                cursor.moveToNext();
            }
            findPreference(Constants.KEY_PREF_ENABLE_QUICK).setEnabled(true);
            if (getPreferenceManager().getSharedPreferences().getBoolean(Constants.KEY_PREF_ENABLE_QUICK, false))
            {
                findPreference(Constants.KEY_PREF_QUICK_BOWLER).setEnabled(true);
                findPreference(Constants.KEY_PREF_QUICK_LEAGUE).setEnabled(true);
            }
        }
        else
        {
            findPreference(Constants.KEY_PREF_ENABLE_QUICK).setEnabled(false);
            findPreference(Constants.KEY_PREF_QUICK_BOWLER).setEnabled(false);
            findPreference(Constants.KEY_PREF_QUICK_LEAGUE).setEnabled(false);
        }

        mArrayBowlerIdsAsStrings = new String[listBowlerIds.size()];
        listBowlerIds.toArray(mArrayBowlerIdsAsStrings);
        mArrayBowlerNames = new String[listBowlerNames.size()];
        listBowlerNames.toArray(mArrayBowlerNames);

        mArrayLeagueIdsAsStrings = new String[listLeagueIds.size()][];
        mArrayLeagueNames = new String[listLeagueNames.size()][];
        for (int i = 0; i < listLeagueIds.size(); i++)
        {
            mArrayLeagueIdsAsStrings[i] = new String[listLeagueIds.get(i).size()];
            listLeagueIds.get(i).toArray(mArrayLeagueIdsAsStrings[i]);
            mArrayLeagueNames[i] = new String[listLeagueNames.get(i).size()];
            listLeagueNames.get(i).toArray(mArrayLeagueNames[i]);
        }

        ListPreference quickBowlerPref = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_BOWLER);
        quickBowlerPref.setEntries(mArrayBowlerNames);
        quickBowlerPref.setEntryValues(mArrayBowlerIdsAsStrings);
        quickBowlerPref.setValueIndex(0);
    }

    private void setPreferenceSummaries()
    {
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

        Preference preference = findPreference(Constants.KEY_PREF_ENABLE_QUICK);
        boolean isQuickEnabled = sharedPreferences.getBoolean(Constants.KEY_PREF_ENABLE_QUICK, false);
        if (isQuickEnabled)
        {
            preference.setSummary(R.string.pref_enable_quick_summary_quick);

            ListPreference listPreference = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_BOWLER);
            mCurrentBowlerPosition = Arrays.binarySearch(mArrayBowlerIdsAsStrings, listPreference.getValue());
            listPreference.setSummary(mArrayBowlerNames[mCurrentBowlerPosition]);

            listPreference = (ListPreference)findPreference(Constants.KEY_PREF_QUICK_LEAGUE);
            int selectedPosition = Arrays.binarySearch(mArrayLeagueIdsAsStrings[mCurrentBowlerPosition], listPreference.getValue());
            listPreference.setSummary(mArrayLeagueNames[mCurrentBowlerPosition][selectedPosition]);
        }
        else
        {
            preference.setSummary(R.string.pref_enable_quick_summary_recent);
            findPreference(Constants.KEY_PREF_QUICK_BOWLER).setSummary("");
            findPreference(Constants.KEY_PREF_QUICK_LEAGUE).setSummary("");
        }

        /*
        Removed disabling pin images - may add back later
        boolean arePinsEnabled = sharedPreferences.getBoolean(Constants.KEY_PREF_ENABLE_PINS, true);
        preference = findPreference(Constants.KEY_PREF_ENABLE_PINS);
        preference.setSummary((arePinsEnabled)
                ? R.string.pref_enable_pins_summary_images
                : R.string.pref_enable_pins_summary_buttons);*/

        String themeColor = sharedPreferences.getString(Constants.KEY_PREF_THEME_COLORS, "Green");
        preference = findPreference(Constants.KEY_PREF_THEME_COLORS);
        preference.setSummary("Current theme is " + themeColor);

        boolean lightThemeVariationEnabled = sharedPreferences.getBoolean(Constants.KEY_PREF_THEME_LIGHT, true);
        preference = findPreference(Constants.KEY_PREF_THEME_LIGHT);
        preference.setSummary((lightThemeVariationEnabled)
                ? R.string.pref_theme_light_summary
                : R.string.pref_theme_dark_summary);
    }
}

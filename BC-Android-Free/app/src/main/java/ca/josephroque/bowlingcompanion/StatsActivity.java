package ca.josephroque.bowlingcompanion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.adapter.StatsAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

public class StatsActivity extends ActionBarActivity
{
    private static final String TAG = "StatsActivity";

    private static final String[] STATS_MIDDLE_GENERAL =
            {"Middle Hit", "Strikes", "Spare Conversions"};

    private static final String[] STATS_MIDDLE_DETAILED =
            {"Head Pins", "Head Pins Spared",
            "Lefts", "Lefts Spared", "Rights", "Rights Spared",
            "Aces", "Aces Spared",
            "Chop Offs", "Chop Offs Spared", "Left Chop Offs", "Left Chop Offs Spared", "Right Chop Offs", "Right Chop Offs Spared",
            "Splits", "Splits Spared", "Left Splits", "Left Splits Spared", "Right Splits", "Right Splits Spared"};

    private static final String[] STATS_FOULS =
            {"Fouls"};

    private static final String[] STATS_PINS_TOTAL =
            {"Pins Left Standing"};

    private static final String[] STATS_PINS_AVERAGE =
            {"Average Pins Left Standing"};

    private static final String[] STATS_GENERAL =
            {"Average", "High Single", "High Series", "Total Pinfall", "# of Games"};

    private static final byte LOADING_BOWLER_STATS = 0;
    private static final byte LOADING_LEAGUE_STATS = 1;
    private static final byte LOADING_GAME_STATS = 2;

    private RecyclerView mStatsRecycler;
    private RecyclerView.Adapter mStatsAdapter;

    private List<String> mListStatNames;
    private List<String> mListStatValues;

    private String mBowlerName;
    private String mLeagueName;
    private long mBowlerId;
    private long mLeagueId;
    private long mGameId;
    private byte mGameNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.primary_green)));

        //Set background color of activity
        getWindow().getDecorView()
                .setBackgroundColor(getResources().getColor(R.color.primary_background));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListStatNames = new ArrayList<>();
        mListStatValues = new ArrayList<>();

        mStatsRecycler = (RecyclerView)findViewById(R.id.recyclerView_stats);
        mStatsRecycler.setHasFixedSize(true);
        mStatsRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mStatsRecycler.setLayoutManager(layoutManager);

        mStatsAdapter = new StatsAdapter(this, mListStatNames, mListStatValues);
        mStatsRecycler.setAdapter(mStatsAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        mBowlerId = preferences.getLong(Constants.PREFERENCE_ID_BOWLER, -1);
        mLeagueId = preferences.getLong(Constants.PREFERENCE_ID_LEAGUE, -1);
        mGameId = preferences.getLong(Constants.PREFERENCE_ID_GAME, -1);

        mListStatNames.clear();
        mListStatValues.clear();

        byte statsToLoad;
        int titleToSet;
        if (mGameId == -1)
        {
            mBowlerName = preferences.getString(Constants.PREFERENCE_NAME_BOWLER, null);
            if (mLeagueId == -1)
            {
                titleToSet = R.string.title_activity_stats_bowler;
                statsToLoad = LOADING_BOWLER_STATS;
            }
            else
            {
                mLeagueName = preferences.getString(Constants.PREFERENCE_NAME_LEAGUE, null);
                titleToSet = R.string.title_activity_stats_league;
                statsToLoad = LOADING_LEAGUE_STATS;
            }
        }
        else
        {
            mGameNumber = getIntent().getByteExtra(Constants.EXTRA_GAME_NUMBER, (byte)-1);
            titleToSet = R.string.title_activity_stats_game;
            statsToLoad = LOADING_GAME_STATS;
        }

        setTitle(titleToSet);
        new LoadStatsTask().execute(statsToLoad);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_settings:
                //TODO: showSettingsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void increaseFirstBallStat(int firstBall, int[] statValues, int offset)
    {
        if (offset > 1 || offset < 0)
            throw new IllegalArgumentException("Offset must be either 0 or 1: " + offset);

        switch(firstBall)
        {
            case 0:
                if (offset == 0)
                {
                    statValues[Constants.STAT_STRIKES]++;
                }
                break;
            case 1:statValues[Constants.STAT_LEFTS + offset]++; break;
            case 2:statValues[Constants.STAT_RIGHTS + offset]++; break;
            case 3:
                statValues[Constants.STAT_LEFT_CHOP_OFFS + offset]++;
                statValues[Constants.STAT_CHOP_OFFS + offset]++;
                break;
            case 4:
                statValues[Constants.STAT_RIGHT_CHOP_OFFS + offset]++;
                statValues[Constants.STAT_CHOP_OFFS + offset]++;
                break;
            case 5:statValues[Constants.STAT_ACES + offset]++; break;
            case 6:
                statValues[Constants.STAT_LEFT_SPLITS + offset]++;
                statValues[Constants.STAT_SPLITS + offset]++;
                break;
            case 7:
                statValues[Constants.STAT_RIGHT_SPLITS + offset]++;
                statValues[Constants.STAT_SPLITS + offset]++;
                break;
            case 8:statValues[Constants.STAT_HEAD_PINS + offset]++;
        }
    }

    private int countPinsLeftStanding(boolean[] thirdBall)
    {
        int pinsLeftStanding = 0;
        for (int i = 0; i < thirdBall.length; i++)
        {
            if (!thirdBall[i])
            {
                switch(i)
                {
                    case 0:case 4:pinsLeftStanding += 2; break;
                    case 1:case 3:pinsLeftStanding += 3; break;
                    case 2:pinsLeftStanding += 5; break;
                }
            }
        }
        return pinsLeftStanding;
    }

    private int getFirstBallValue(boolean[] firstBall)
    {
        if (!firstBall[2])
        {
            return -1;
        }

        int numberOfPinsKnockedDown = 0;
        for (boolean knockedDown: firstBall)
        {
            if (knockedDown)
                numberOfPinsKnockedDown++;
        }

        if (numberOfPinsKnockedDown == 5)
            return 0;       //STRIKE
        else if (numberOfPinsKnockedDown == 4)
        {
            if (!firstBall[0])
                return 1;   //LEFT
            else if (!firstBall[4])
                return 2;   //RIGHT
        }
        else if (numberOfPinsKnockedDown == 3)
        {
            if (!firstBall[3] && !firstBall[4])
                return 3;   //LEFT CHOP
            else if (!firstBall[0] && !firstBall[1])
                return 4;   //RIGHT CHOP
            else if (!firstBall[0] && !firstBall[4])
                return 5;   //ACE
        }
        else if (numberOfPinsKnockedDown == 2)
        {
            if (firstBall[1])
                return 6;   //LEFT SPLIT
            else if (firstBall[3])
                return 7;   //RIGHT SPLIT
        }
        else
            return 8;       //HEAD PIN

        return -2;
    }

    private void setGeneralAndDetailedStatValues(final int[] statValues, final int totalShotsAtMiddle, final int spareChances, final int statOffset)
    {
        int currentStatPosition = statOffset;
        final DecimalFormat decimalFormat = new DecimalFormat("##0.#");
        if (statValues[Constants.STAT_MIDDLE_HIT] > 0)
        {
            mListStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_MIDDLE_HIT] / (double)totalShotsAtMiddle * 100)
                            + "% [" + statValues[Constants.STAT_MIDDLE_HIT] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_STRIKES] > 0)
        {
            mListStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_STRIKES] / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[Constants.STAT_STRIKES] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_SPARE_CONVERSIONS] > 0)
        {
            mListStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_SPARE_CONVERSIONS] / (double) spareChances * 100)
                            + "% [" + statValues[Constants.STAT_SPARE_CONVERSIONS] + "/" + spareChances + "]");
        }
        currentStatPosition++;

        for (int i = Constants.STAT_HEAD_PINS; i < Constants.STAT_RIGHT_SPLITS_SPARED; i += 2, currentStatPosition += 2)
        {
            if (statValues[i] > 0)
            {
                mListStatValues.set(currentStatPosition,
                        decimalFormat.format(statValues[i] / (double) totalShotsAtMiddle * 100)
                                + "% [" + statValues[i] + "/" + totalShotsAtMiddle + "]");
            }
            if (statValues[i + 1] > 0)
            {
                mListStatValues.set(currentStatPosition + 1,
                        decimalFormat.format(statValues[i + 1] / (double)statValues[i] * 100)
                                + "% [" + statValues[i + 1] + "/" + statValues[i] + "]");
            }
        }

        final int statValuesListSize = mListStatValues.size();
        for (int i = Constants.STAT_FOULS; i <= Constants.STAT_NUMBER_OF_GAMES && statValuesListSize > currentStatPosition; i++, currentStatPosition++)
        {
            mListStatValues.set(currentStatPosition, String.valueOf(statValues[i]));
        }
    }

    private Cursor getBowlerOrLeagueCursor(boolean shouldGetLeagueStats)
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_ACCESSED + ", "
                + FrameEntry.COLUMN_NAME_FOULS + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2]
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " LEFT JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=" + FrameEntry.COLUMN_NAME_GAME_ID
                + ((shouldGetLeagueStats)
                ? " WHERE game." + GameEntry.COLUMN_NAME_LEAGUE_ID + "=?"
                : " WHERE game." + GameEntry.COLUMN_NAME_BOWLER_ID + "=?")
                + " ORDER BY game." + GameEntry._ID + ", frame." + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {(shouldGetLeagueStats)
                ? String.valueOf(mBowlerId)
                : String.valueOf(mLeagueId)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    private Cursor getGameCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
        String rawStatsQuery = "SELECT "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_ACCESSED + ", "
                + FrameEntry.COLUMN_NAME_FOULS + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2]
                + " FROM " + FrameEntry.TABLE_NAME
                + " WHERE " + FrameEntry.COLUMN_NAME_GAME_ID + "=?"
                + " ORDER BY " + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(mGameId)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    private class LoadStatsTask extends AsyncTask<Byte, Void, Void>
    {
        @Override
        protected Void doInBackground(Byte... bowlerLeagueOrGameParam)
        {
            final byte bowlerLeagueOrGame = bowlerLeagueOrGameParam[0];
            final byte NUMBER_OF_GENERAL_DETAILS;
            Cursor cursor;
            int[] statValues;
            mListStatNames.add("Bowler");
            mListStatValues.add(mBowlerName);

            mListStatNames.addAll(Arrays.asList(STATS_MIDDLE_GENERAL));
            mListStatNames.addAll(Arrays.asList(STATS_MIDDLE_DETAILED));
            mListStatNames.addAll(Arrays.asList(STATS_FOULS));
            mListStatNames.addAll(Arrays.asList(STATS_PINS_TOTAL));
            switch(bowlerLeagueOrGame)
            {
                case LOADING_BOWLER_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 1;
                    mListStatNames.addAll(Arrays.asList(STATS_PINS_AVERAGE));
                    mListStatNames.addAll(Arrays.asList(STATS_GENERAL));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                            + STATS_GENERAL.length];
                    cursor = getBowlerOrLeagueCursor(false);
                    break;
                case LOADING_LEAGUE_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 2;
                    mListStatNames.add(1, "League");
                    mListStatValues.add(1, mLeagueName);
                    mListStatNames.addAll(Arrays.asList(STATS_PINS_AVERAGE));
                    mListStatNames.addAll(Arrays.asList(STATS_GENERAL));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                            + STATS_GENERAL.length];
                    cursor = getBowlerOrLeagueCursor(true);
                    break;
                case LOADING_GAME_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 3;
                    mListStatNames.add(1, "League");
                    mListStatValues.add(1, mLeagueName);
                    mListStatNames.add(2, "Game #");
                    mListStatValues.add(2, String.valueOf(mGameNumber));

                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length];
                    cursor = getGameCursor();
                    break;
                default:
                    throw new IllegalArgumentException("bowlerLeagueOrGame must be between 0 and 2 (inclusive");
            }

            int i = mListStatValues.size();
            while (i < mListStatNames.size())
            {
                mListStatValues.add("--");
                i++;
            }

            int totalShotsAtMiddle = 0;
            int spareChances = 0;
            int seriesTotal = 0;

            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED)) == 1);
                    if (bowlerLeagueOrGame == 2 && !frameAccessed)
                        break;

                    byte frameNumber = (byte)cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_NUMBER));
                    String frameFouls = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                    String[] ballStrings = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[0])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[1])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[2]))};
                    boolean[][] pinState = new boolean[3][5];
                    for (i = 0; i < 5; i++)
                    {
                        pinState[0][i] = ballStrings[0].charAt(i) == '1';
                        pinState[1][i] = ballStrings[1].charAt(i) == '1';
                        pinState[2][i] = ballStrings[2].charAt(i) == '1';
                    }
                    for (i = 1; i <= 3; i++)
                    {
                        if (frameFouls.contains(String.valueOf(i)))
                            statValues[Constants.STAT_FOULS]++;
                    }

                    if (bowlerLeagueOrGame == LOADING_GAME_STATS || frameAccessed)
                    {
                        if (frameNumber == Constants.NUMBER_OF_FRAMES)
                        {
                            totalShotsAtMiddle++;
                            int firstBall = getFirstBallValue(pinState[0]);
                            if (firstBall != -1)
                                statValues[Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(firstBall, statValues, 0);
                            if (firstBall < 5)
                                spareChances++;

                            if (firstBall != 0)
                            {
                                if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                {
                                    statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                    increaseFirstBallStat(firstBall, statValues, 1);
                                }
                                else
                                {
                                    statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                }
                            }
                            else
                            {
                                totalShotsAtMiddle++;
                                int secondBall = getFirstBallValue(pinState[1]);
                                if (secondBall != -1)
                                    statValues[Constants.STAT_MIDDLE_HIT]++;
                                increaseFirstBallStat(secondBall, statValues, 0);

                                if (secondBall != 0)
                                {
                                    if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                    {
                                        statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                        increaseFirstBallStat(secondBall, statValues, 1);
                                    }
                                    else
                                    {
                                        statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                    }
                                }
                                else
                                {
                                    totalShotsAtMiddle++;
                                    int thirdBall = getFirstBallValue(pinState[2]);
                                    if (thirdBall != -1)
                                        statValues[Constants.STAT_MIDDLE_HIT]++;
                                    increaseFirstBallStat(thirdBall, statValues, 0);

                                    if (thirdBall != 0)
                                    {
                                        statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                    }
                                }
                            }
                        }
                        else
                        {
                            totalShotsAtMiddle++;
                            int firstBall = getFirstBallValue(pinState[0]);
                            if (firstBall != -1)
                                statValues[Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(firstBall, statValues, 0);

                            if (firstBall < 5)
                                spareChances++;

                            if (firstBall != 0)
                            {
                                if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                {
                                    statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                    increaseFirstBallStat(firstBall, statValues, 1);
                                }
                                else
                                {
                                    statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                }
                            }
                        }
                    }

                    if (bowlerLeagueOrGame != LOADING_BOWLER_STATS && frameNumber == 1)
                    {
                        short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));
                        byte gameNumber = (byte)cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_NUMBER));
                        if (statValues[Constants.STAT_HIGH_SINGLE] < gameScore)
                            statValues[Constants.STAT_HIGH_SINGLE] = gameScore;
                        statValues[Constants.STAT_TOTAL_PINFALL] += gameScore;
                        statValues[Constants.STAT_NUMBER_OF_GAMES]++;

                        if (gameNumber == 1)
                        {
                            if (statValues[Constants.STAT_HIGH_SERIES] < seriesTotal)
                                statValues[Constants.STAT_HIGH_SERIES] = seriesTotal;
                            seriesTotal = gameScore;
                        }
                        else
                        {
                            seriesTotal += gameScore;
                        }
                    }
                    cursor.moveToNext();
                }
            }

            if (bowlerLeagueOrGame != LOADING_GAME_STATS)
            {
                if (statValues[Constants.STAT_HIGH_SERIES] < seriesTotal)
                {
                    statValues[Constants.STAT_HIGH_SERIES] = seriesTotal;
                }

                if (statValues[Constants.STAT_NUMBER_OF_GAMES] > 0)
                {
                    statValues[Constants.STAT_AVERAGE] =
                            statValues[Constants.STAT_TOTAL_PINFALL] / statValues[Constants.STAT_NUMBER_OF_GAMES];
                    statValues[Constants.STAT_AVERAGE_PINS_LEFT_ON_DECK] =
                            statValues[Constants.STAT_PINS_LEFT_ON_DECK] / statValues[Constants.STAT_NUMBER_OF_GAMES];
                }
            }
            setGeneralAndDetailedStatValues(statValues, totalShotsAtMiddle, spareChances, NUMBER_OF_GENERAL_DETAILS);
            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mStatsAdapter.notifyDataSetChanged();
        }
    }
}

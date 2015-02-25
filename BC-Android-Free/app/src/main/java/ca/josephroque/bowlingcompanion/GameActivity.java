package ca.josephroque.bowlingcompanion;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.data.GameScore;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;


public class GameActivity extends ActionBarActivity
{

    private static final String TAG = "GameActivity";
    private static final String TITLE_DRAWER = "Game Options";

    private int COLOR_BACKGROUND;
    private int COLOR_HIGHLIGHT;

    private static final byte GAME_DEFAULT = 0;
    private static final byte LISTENER_TEXT_FRAMES = 0;
    private static final byte LISTENER_PIN_BUTTONS = 1;
    private static final byte LISTENER_OTHER = 2;

    private long[] mGameIds;
    private long[] mFrameIds;
    private byte mNumberOfGames;

    private byte mCurrentGame = 0;
    private byte mCurrentFrame = 0;
    private byte mCurrentBall = 0;
    private boolean[] mHasFrameBeenAccessed;
    private boolean mEventMode;
    private boolean[][][] mPinState;
    private boolean[][] mFouls;
    private short[] mGameScores;
    private short[] mGameScoresMinusFouls;

    private String mActivityTitle;
    private List<String> navigationDrawerOptions;

    private TextView[][] mTextViewBallScores;
    private TextView[][] mTextViewFouls;
    private TextView[] mTextViewFrames;
    private HorizontalScrollView hsvFrames;
    private TextView mTextViewFinalScore;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mActivityTitle = getTitle().toString();
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.primary_green)));

        //Set background color of activity
        getWindow().getDecorView()
                .setBackgroundColor(getResources().getColor(R.color.primary_background));

        COLOR_BACKGROUND = getResources().getColor(android.R.color.transparent);
        COLOR_HIGHLIGHT = getResources().getColor(android.R.color.white);

        AdView adView = (AdView) findViewById(R.id.adView_game);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        hsvFrames = (HorizontalScrollView)findViewById(R.id.hsv_frames);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams;
        navigationDrawerOptions = new ArrayList<>();
        mTextViewBallScores = new TextView[Constants.NUMBER_OF_FRAMES][3];
        mTextViewFouls = new TextView[Constants.NUMBER_OF_FRAMES][3];
        mTextViewFrames = new TextView[Constants.NUMBER_OF_FRAMES];
        mPinState = new boolean[Constants.NUMBER_OF_FRAMES][3][5];
        mFouls = new boolean[Constants.NUMBER_OF_FRAMES][3];
        mHasFrameBeenAccessed = new boolean[Constants.NUMBER_OF_FRAMES];

        final View.OnClickListener[] onClickListeners = getOnClickListeners();

        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            TextView frameText = new TextView(this);
            switch(i)
            {
                case 0: frameText.setId(R.id.text_frame_0); break;
                case 1: frameText.setId(R.id.text_frame_1); break;
                case 2: frameText.setId(R.id.text_frame_2); break;
                case 3: frameText.setId(R.id.text_frame_3); break;
                case 4: frameText.setId(R.id.text_frame_4); break;
                case 5: frameText.setId(R.id.text_frame_5); break;
                case 6: frameText.setId(R.id.text_frame_6); break;
                case 7: frameText.setId(R.id.text_frame_7); break;
                case 8: frameText.setId(R.id.text_frame_8); break;
                case 9: frameText.setId(R.id.text_frame_9); break;
            }
            frameText.setBackgroundResource(R.drawable.background_frame_text);
            frameText.setGravity(Gravity.CENTER);
            frameText.setOnClickListener(onClickListeners[LISTENER_TEXT_FRAMES]);
            layoutParams = new RelativeLayout.LayoutParams(getPixelsFromDP(120), getPixelsFromDP(88));
            layoutParams.leftMargin = getPixelsFromDP(120 * i);
            layoutParams.topMargin = getPixelsFromDP(40);
            relativeLayout.addView(frameText, layoutParams);
            mTextViewFrames[i] = frameText;

            for (int j = 0; j < 3; j++)
            {
                TextView text = new TextView(this);
                text.setBackgroundResource(R.drawable.background_frame_text);
                text.setGravity(Gravity.CENTER);
                layoutParams = new RelativeLayout.LayoutParams(getPixelsFromDP(40), getPixelsFromDP(40));
                layoutParams.leftMargin = getPixelsFromDP(120 * i + j * 40);
                layoutParams.topMargin = 0;
                relativeLayout.addView(text, layoutParams);
                mTextViewBallScores[i][j] = text;

                text = new TextView(this);
                text.setGravity(Gravity.CENTER);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                layoutParams = new RelativeLayout.LayoutParams(getPixelsFromDP(40), getPixelsFromDP(20));
                layoutParams.leftMargin = getPixelsFromDP(120 * i + j * 40);
                layoutParams.topMargin = getPixelsFromDP(40);
                relativeLayout.addView(text, layoutParams);
                mTextViewFouls[i][j] = text;
            }

            TextView textFrameNumber = new TextView(this);
            textFrameNumber.setText(String.valueOf(i + 1));
            textFrameNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            textFrameNumber.setGravity(Gravity.CENTER_HORIZONTAL);
            layoutParams = new RelativeLayout.LayoutParams(getPixelsFromDP(120), getPixelsFromDP(36));
            layoutParams.leftMargin = getPixelsFromDP(120 * i);
            layoutParams.topMargin = getPixelsFromDP(128);
            relativeLayout.addView(textFrameNumber, layoutParams);
        }

        mTextViewFinalScore = new TextView(this);
        mTextViewFinalScore.setGravity(Gravity.CENTER);
        mTextViewFinalScore.setBackgroundResource(R.drawable.background_frame_text);
        layoutParams = new RelativeLayout.LayoutParams(getPixelsFromDP(120), getPixelsFromDP(128));
        layoutParams.leftMargin = getPixelsFromDP(Constants.NUMBER_OF_FRAMES * 120);
        layoutParams.topMargin = 0;
        relativeLayout.addView(mTextViewFinalScore, layoutParams);
        hsvFrames.addView(relativeLayout);

        findViewById(R.id.imageView_next_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.imageView_prev_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.textView_next_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.textView_prev_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        //TODO: uncomment when added
        //findViewById(R.id.imageView_foul).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        //findViewById(R.id.imageView_reset_frame).setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.game_drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer_games);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch(position)
                {
                    case 0:
                        mDrawerLayout.closeDrawer(mDrawerList);
                        Intent mainIntent = new Intent(GameActivity.this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    case 1:
                        mDrawerLayout.closeDrawer(mDrawerList);
                        Intent leagueIntent = new Intent(GameActivity.this, LeagueEventActivity.class);
                        leagueIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(leagueIntent);
                    case 2:
                        if (!mEventMode)
                        {
                            mDrawerLayout.closeDrawer(mDrawerList);
                            Intent seriesIntent = new Intent(GameActivity.this, SeriesActivity.class);
                            seriesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(seriesIntent);
                            break;
                        }
                    default:
                        long[] targetFrames = new long[10];
                        System.arraycopy(mFrameIds,
                                mCurrentGame * Constants.NUMBER_OF_FRAMES,
                                targetFrames,
                                0,
                                Constants.NUMBER_OF_FRAMES);
                        saveGameToDatabase(GameActivity.this,
                                mGameIds[mCurrentGame],
                                targetFrames,
                                mHasFrameBeenAccessed,
                                mPinState,
                                mFouls,
                                mGameScoresMinusFouls[mCurrentGame]);
                        setScoresInNavigationDrawer();
                        loadGameFromDatabase((byte)(position - (mEventMode ? 2:3)));
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                }
            }
        });

        //TODO: include ic_drawer if needed
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, /*R.drawable.ic_drawer,*/ R.string.drawer_open, R.string.drawer_close)
        {
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view)
            {
                super.onDrawerOpened(view);
                getSupportActionBar().setTitle(TITLE_DRAWER);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerAdapter = new ArrayAdapter<>(this, R.layout.list_game_navigation, navigationDrawerOptions);
        mDrawerList.setAdapter(mDrawerAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Intent intent = getIntent();
        mEventMode = intent.getBooleanExtra(Constants.EXTRA_EVENT_MODE, false);
        mGameIds = intent.getLongArrayExtra(Constants.EXTRA_ARRAY_GAME_IDS);
        mFrameIds = intent.getLongArrayExtra(Constants.EXTRA_ARRAY_FRAME_IDS);
        mNumberOfGames = (byte)mGameIds.length;

        mGameScores = new short[mNumberOfGames];
        mGameScoresMinusFouls = new short[mNumberOfGames];

        navigationDrawerOptions.clear();
        navigationDrawerOptions.add("Bowlers");
        navigationDrawerOptions.add("Leagues");
        if (!mEventMode)
            navigationDrawerOptions.add("Series");
        for (int i = 0; i < mNumberOfGames; i++)
        {
            navigationDrawerOptions.add("Game " + (i + 1));
        }

        setScoresInNavigationDrawer();
        loadGameFromDatabase(GAME_DEFAULT);
    }

    @Override
    protected void onPause()
    {
        clearFrameColor();
        long[] targetFrames = new long[Constants.NUMBER_OF_FRAMES];
        System.arraycopy(mFrameIds,
                mCurrentGame * Constants.NUMBER_OF_FRAMES,
                targetFrames,
                0,
                Constants.NUMBER_OF_FRAMES);
        saveGameToDatabase(this,
                mGameIds[mCurrentGame],
                targetFrames,
                mHasFrameBeenAccessed,
                mPinState,
                mFouls,
                mGameScoresMinusFouls[mCurrentGame]);

        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_game_stats).setVisible(!drawerOpen);
        //menu.findItem(R.id.action_game_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
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
            case R.id.action_stats:
                showGameStats();
                return true;
            case R.id.action_settings:
                //TODO: showSettingsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showGameStats()
    {
        clearFrameColor();
        getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCE_ID_GAME, mGameIds[mCurrentGame])
                .apply();

        Intent statsIntent = new Intent(GameActivity.this, StatsActivity.class);
        statsIntent.putExtra(Constants.EXTRA_GAME_NUMBER, (byte)(mCurrentGame + 1));
        startActivity(statsIntent);
    }

    private View.OnClickListener[] getOnClickListeners()
    {
        View.OnClickListener[] listeners = new View.OnClickListener[3];
        listeners[LISTENER_TEXT_FRAMES] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                byte frameToSet = 0;
                switch(v.getId())
                {
                    case R.id.text_frame_9: frameToSet++;
                    case R.id.text_frame_8: frameToSet++;
                    case R.id.text_frame_7: frameToSet++;
                    case R.id.text_frame_6: frameToSet++;
                    case R.id.text_frame_5: frameToSet++;
                    case R.id.text_frame_4: frameToSet++;
                    case R.id.text_frame_3: frameToSet++;
                    case R.id.text_frame_2: frameToSet++;
                    case R.id.text_frame_1: frameToSet++;
                    case R.id.text_frame_0:
                        clearFrameColor();
                        mCurrentFrame = frameToSet;
                        mCurrentBall = 0;
                        for (int i = mCurrentFrame; i >= 0; i--)
                        {
                            if (mHasFrameBeenAccessed[i])
                                break;
                            mHasFrameBeenAccessed[i] = true;
                        }
                        updateFrameColor();
                        break;
                    default:
                        throw new RuntimeException("Invalid frame id");
                }
            }
        };

        listeners[LISTENER_PIN_BUTTONS] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                byte ballToSet = 0;
                switch(v.getId())
                {
                    //TODO: uncomment lines below when final pin layout decided
                    /*
                    case R.id.button_pin_4: ballToSet++;
                    case R.id.button_pin_3: ballToSet++;
                    case R.id.button_pin_2: ballToSet++;
                    case R.id.button_pin_1: ballToSet++;
                    case R.id.button_pin_0:
                        alterPinState(ballToSet);
                        break;
                    default:
                        throw new RuntimeException("Invalid pin button id");
                     */
                }
            }
        };

        listeners[LISTENER_OTHER] = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch(v.getId())
                {
                    /*
                    TODO: uncomment when added
                    case R.id.imageView_reset_frame:
                        clearFrameColor();
                        mCurrentBall = 0;
                        for (int i = 0; i < 3; i++)
                        {
                            mFouls[mCurrentFrame][i] = false;
                            for (int j = 0; j < 5; j++)
                                mPinState[mCurrentFrame][i][j] = false;
                        }
                        updateFrameColor();
                        updateBalls(mCurrentFrame);
                        updateScore();
                        break;
                    case R.id.imageView_foul:
                        mFouls[mCurrentFrame][mCurrentBall] = !mFouls[mCurrentFrame][mCurrentBall];
                        mTextViewFouls[mCurrentFrame][mCurrentBall].post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mTextViewFouls[mCurrentFrame][mCurrentBall]
                                        .setText(mFouls[mCurrentFrame][mCurrentBall]
                                        ? "F"
                                        : "");
                            }
                        });
                        updateFouls();
                        break;*/
                    case R.id.imageView_next_ball:
                    case R.id.textView_next_ball:
                        if (mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 2)
                            return;

                        clearFrameColor();
                        if (Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN))
                        {
                            if (mCurrentFrame < Constants.LAST_FRAME)
                            {
                                mCurrentBall = 0;
                                mCurrentFrame++;
                            }
                            else if (mCurrentBall < 2)
                            {
                                mCurrentBall++;
                            }
                        }
                        else if (++mCurrentBall == 3)
                        {
                            mCurrentBall = 0;
                            ++mCurrentFrame;
                        }
                        mHasFrameBeenAccessed[mCurrentFrame] = true;
                        updateFrameColor();
                        break;
                    case R.id.imageView_prev_ball:
                    case R.id.textView_prev_ball:
                        if (mCurrentFrame == 0 && mCurrentBall == 0)
                            return;

                        clearFrameColor();
                        if (--mCurrentBall == -1)
                        {
                            mCurrentBall = 0;
                            --mCurrentFrame;
                            while(!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN) && mCurrentBall < 2)
                            {
                                mCurrentBall++;
                            }
                        }
                        updateFrameColor();
                        break;
                    default:
                        throw new RuntimeException("Unknown other button id");
                }
            }
        };

        return listeners;
    }

    private void updateBalls(final int frameToUpdate)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final String ballString[] = new String[3];
                if (frameToUpdate == Constants.LAST_FRAME)
                {
                    if (Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN))
                    {
                        ballString[0] = Constants.BALL_STRIKE;
                        if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                        {
                            ballString[1] = Constants.BALL_STRIKE;
                            ballString[2] = GameScore.getValueOfBall(mPinState[frameToUpdate][2], 2, true);
                        }
                        else
                        {
                            ballString[1] = GameScore.getValueOfBall(mPinState[frameToUpdate][1], 1, false);
                            if (Arrays.equals(mPinState[frameToUpdate][2], Constants.FRAME_PINS_DOWN))
                                ballString[2] = Constants.BALL_SPARE;
                            else
                                ballString[2] = GameScore.getValueOfBallDifference(mPinState[frameToUpdate], 2, false);
                        }
                    }
                    else
                    {
                        ballString[0] = GameScore.getValueOfBall(mPinState[frameToUpdate][0], 0, false);
                        if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                        {
                            ballString[1] = Constants.BALL_SPARE;
                            ballString[2] = GameScore.getValueOfBall(mPinState[frameToUpdate][2], 2, true);
                        }
                        else
                        {
                            ballString[1] = GameScore.getValueOfBallDifference(mPinState[frameToUpdate], 1, false);
                            ballString[2] = GameScore.getValueOfBallDifference(mPinState[frameToUpdate], 2, false);
                        }
                    }
                }
                else
                {
                    ballString[0] = GameScore.getValueOfBallDifference(mPinState[frameToUpdate], 0, false);
                    if (!Arrays.equals(mPinState[frameToUpdate][0], Constants.FRAME_PINS_DOWN))
                    {
                        if (Arrays.equals(mPinState[frameToUpdate][1], Constants.FRAME_PINS_DOWN))
                        {
                            ballString[1] = Constants.BALL_SPARE;
                            ballString[2] = Constants.BALL_EMPTY;
                        }
                        else
                        {
                            ballString[1] = GameScore.getValueOfBallDifference(mPinState[frameToUpdate], 1, false);
                            ballString[2] = GameScore.getValueOfBallDifference(mPinState[frameToUpdate], 2, false);
                        }
                    }
                    else
                    {
                        ballString[1] = Constants.BALL_EMPTY;
                        ballString[2] = Constants.BALL_EMPTY;
                    }
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < 3; i++)
                        {
                            mTextViewBallScores[frameToUpdate][i].setText(ballString[i]);
                            mTextViewFouls[frameToUpdate][i].setText(
                                    (mFouls[frameToUpdate][i])
                                            ? "F"
                                            : "");
                        }
                    }
                });
            }
        }).start();
    }

    private void updateScore()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final short[] frameScores = new short[Constants.NUMBER_OF_FRAMES];
                for (int f = Constants.LAST_FRAME; f >= 0; f--)
                {
                    if (f == Constants.LAST_FRAME)
                    {
                        for (int b = 2; b >= 0; b--)
                        {
                            switch(b)
                            {
                                case 2:
                                    frameScores[f] += GameScore.getValueOfFrame(mPinState[f][b]);
                                    break;
                                case 1:
                                case 0:
                                    if (Arrays.equals(mPinState[f][b], Constants.FRAME_PINS_DOWN))
                                    {
                                        frameScores[f] += GameScore.getValueOfFrame(mPinState[f][b]);
                                    }
                                    break;
                                default: //do nothing
                            }
                        }
                    }
                    else
                    {
                        for (int b = 0; b < 3; b++)
                        {
                            if (b < 2 && Arrays.equals(mPinState[f][b], Constants.FRAME_PINS_DOWN))
                            {
                                frameScores[f] += GameScore.getValueOfFrame(mPinState[f][b]);
                                frameScores[f] += GameScore.getValueOfFrame(mPinState[f + 1][0]);
                                if (b == 0)
                                {
                                    if (f == Constants.LAST_FRAME - 1)
                                    {
                                        if (frameScores[f] == 30)
                                        {
                                            frameScores[f] += GameScore.getValueOfFrame(mPinState[f + 1][1]);
                                        }
                                        else
                                        {
                                            frameScores[f] += GameScore.getValueOfFrameDifference(mPinState[f + 1][0], mPinState[f + 1][1]);
                                        }
                                    }
                                    else if (frameScores[f] < 30)
                                    {
                                        frameScores[f] += GameScore.getValueOfFrameDifference(mPinState[f + 1][0], mPinState[f + 1][1]);
                                    }
                                    else
                                    {
                                        frameScores[f] += GameScore.getValueOfFrame(mPinState[f + 2][0]);
                                    }
                                }
                                break;
                            }
                            else if (b == 2)
                            {
                                frameScores[f] += GameScore.getValueOfFrame(mPinState[f][b]);
                            }
                        }
                    }
                }

                short totalScore = 0;
                for (int i = 0; i < frameScores.length; i++)
                {
                    totalScore += frameScores[i];
                    frameScores[i] = totalScore;
                }
                mGameScores[mCurrentGame] = totalScore;

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < frameScores.length; i++)
                        {
                            mTextViewFrames[i].setText(String.valueOf(frameScores[i]));
                        }
                        navigationDrawerOptions.set(mCurrentGame + ((mEventMode) ? 2:3),
                                "Game " + (mCurrentGame + 1) + "(" + mGameScores[mCurrentGame] + ")");
                        mDrawerAdapter.notifyDataSetChanged();
                    }
                });
                updateFouls();
            }
        }).start();
    }

    private void updateFouls()
    {
        byte foulCount = 0;
        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (mFouls[i][j])
                    foulCount++;
            }
        }
        short scoreWithFouls = (short)(mGameScores[mCurrentGame] - 15 * foulCount);
        if (scoreWithFouls < 0)
            scoreWithFouls = 0;
        mGameScoresMinusFouls[mCurrentGame] = scoreWithFouls;

        mTextViewFinalScore.post(new Runnable()
        {
            @Override
            public void run()
            {
                mTextViewFinalScore.setText(String.valueOf(mGameScoresMinusFouls[mCurrentGame]));
            }
        });
    }

    private void clearFrameColor()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GradientDrawable drawable = (GradientDrawable)
                        mTextViewBallScores[mCurrentFrame][mCurrentBall].getBackground();
                drawable.setColor(COLOR_BACKGROUND);
                drawable = (GradientDrawable)
                        mTextViewFrames[mCurrentFrame].getBackground();
                drawable.setColor(COLOR_BACKGROUND);
            }
        });
    }

    private void updateFrameColor()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                GradientDrawable drawable = (GradientDrawable)
                        mTextViewBallScores[mCurrentFrame][mCurrentBall].getBackground();
                drawable.setColor(COLOR_HIGHLIGHT);
                drawable = (GradientDrawable)
                        mTextViewFrames[mCurrentFrame].getBackground();
                drawable.setColor(COLOR_HIGHLIGHT);

                //TODO: set color of pin buttons

                focusOnFrame();
            }
        });
    }

    private static void saveGameToDatabase(
            final Activity srcActivity,
            final long gameId,
            final long[] frameIds,
            final boolean[] hasFrameBeenAccessed,
            final boolean[][][] pinState,
            final boolean[][] fouls,
            final short finalScore)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database = DatabaseHelper.getInstance(srcActivity).getWritableDatabase();
                ContentValues values;

                database.beginTransaction();
                try
                {
                    values = new ContentValues();
                    values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, finalScore);
                    database.update(GameEntry.TABLE_NAME,
                            values,
                            GameEntry._ID + "=?",
                            new String[]{String.valueOf(gameId)});

                    for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
                    {
                        StringBuilder foulsOfFrame = new StringBuilder();
                        for (int ballCount = 0; ballCount < 3; ballCount++)
                        {
                            if (fouls[i][ballCount])
                                foulsOfFrame.append(ballCount + 1);
                        }
                        if (foulsOfFrame.length() == 0)
                            foulsOfFrame.append(0);

                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_NAME_BALL[0], GameScore.booleanFrameToString(pinState[i][0]));
                        values.put(FrameEntry.COLUMN_NAME_BALL[1], GameScore.booleanFrameToString(pinState[i][1]));
                        values.put(FrameEntry.COLUMN_NAME_BALL[2], GameScore.booleanFrameToString(pinState[i][2]));
                        values.put(FrameEntry.COLUMN_NAME_FRAME_ACCESSED, (hasFrameBeenAccessed[i]) ? 1:0);
                        values.put(FrameEntry.COLUMN_NAME_FOULS, foulsOfFrame.toString());
                        database.update(FrameEntry.TABLE_NAME,
                                values,
                                FrameEntry._ID + "=?",
                                new String[]{String.valueOf(frameIds[i])});
                    }
                    database.setTransactionSuccessful();
                }
                catch (Exception ex)
                {
                    Log.w(TAG, "Error saving game " + gameId);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }

    private void loadGameFromDatabase(final byte newGame)
    {
        clearFrameColor();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                mCurrentGame = newGame;
                SQLiteDatabase database = DatabaseHelper.getInstance(GameActivity.this).getReadableDatabase();

                Cursor cursor = database.query(FrameEntry.TABLE_NAME,
                        new String[]{FrameEntry.COLUMN_NAME_FRAME_ACCESSED, FrameEntry.COLUMN_NAME_BALL[0], FrameEntry.COLUMN_NAME_BALL[1], FrameEntry.COLUMN_NAME_BALL[2], FrameEntry.COLUMN_NAME_FOULS},
                        FrameEntry.COLUMN_NAME_GAME_ID + "=?",
                        new String[]{String.valueOf(mGameIds[mCurrentGame])},
                        null,
                        null,
                        FrameEntry.COLUMN_NAME_FRAME_NUMBER);

                mFouls = new boolean[Constants.NUMBER_OF_FRAMES][3];
                byte currentFrameIterator = 0;
                if (cursor.moveToFirst())
                {
                    while(!cursor.isAfterLast())
                    {
                        byte frameAccessed = (byte)cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED));
                        mHasFrameBeenAccessed[currentFrameIterator] = (frameAccessed == 1);
                        for (int i = 0; i < 3; i++)
                        {
                            String ballString = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[i]));
                            boolean[] ballBoolean = {GameScore.getBoolean(ballString.charAt(0)), GameScore.getBoolean(ballString.charAt(1)), GameScore.getBoolean(ballString.charAt(2)), GameScore.getBoolean(ballString.charAt(3)), GameScore.getBoolean(ballString.charAt(4))};
                            mPinState[currentFrameIterator][i] = ballBoolean;
                        }
                        String foulsOfFrame = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                        for (int ballCount = 0; ballCount < 3; ballCount++)
                        {
                            mFouls[currentFrameIterator][ballCount] = foulsOfFrame.contains(String.valueOf(ballCount + 1));
                        }

                        currentFrameIterator++;
                        cursor.moveToNext();
                    }
                }

                mCurrentFrame = 0;
                mCurrentBall = 0;
                updateScore();

                for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
                    updateBalls(i);
                mHasFrameBeenAccessed[0] = true;
                updateFrameColor();
            }
        }).start();
    }

    private void focusOnFrame()
    {
        hsvFrames.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCurrentFrame >= 1)
                    hsvFrames.smoothScrollTo(mTextViewFrames[mCurrentFrame - 1].getLeft(), 0);
                else
                    hsvFrames.smoothScrollTo(mTextViewFrames[mCurrentFrame].getLeft(), 0);
            }
        });
    }

    private void setScoresInNavigationDrawer()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database = DatabaseHelper.getInstance(GameActivity.this).getReadableDatabase();

                StringBuilder whereBuilder = new StringBuilder(GameEntry._ID + "=?");
                String[] whereArgs = new String[mNumberOfGames];
                whereArgs[0] = String.valueOf(mGameIds[0]);
                for (int i = 1; i < mNumberOfGames; i++)
                {
                    whereBuilder.append(" OR ");
                    whereBuilder.append(GameEntry._ID);
                    whereBuilder.append("=?");
                    whereArgs[i] = String.valueOf(mGameIds[i]);
                }

                Cursor cursor = database.query(GameEntry.TABLE_NAME,
                        new String[]{GameEntry.COLUMN_NAME_GAME_FINAL_SCORE},
                        whereBuilder.toString(),
                        whereArgs,
                        null,
                        null,
                        GameEntry._ID);

                final int startingGamePosition = mEventMode ? 1:2;
                int currentGamePosition = startingGamePosition + 1;
                if (cursor.moveToFirst())
                {
                    while(!cursor.isAfterLast())
                    {
                        short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));
                        navigationDrawerOptions.set(currentGamePosition, "Game "
                                + (currentGamePosition - startingGamePosition)
                                + "(" + gameScore + ")");
                        currentGamePosition++;
                        cursor.moveToNext();
                    }
                }
                else
                {
                    throw new RuntimeException("No games found - cannot set scores");
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mDrawerAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private int getPixelsFromDP(int dps)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dps * scale + 0.5f);
    }
}

package ca.josephroque.bowlingcompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
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
import ca.josephroque.bowlingcompanion.theme.ChangeableThemedActivity;
import ca.josephroque.bowlingcompanion.theme.Theme;


public class GameActivity extends ActionBarActivity
    implements ChangeableThemedActivity
{

    /** Tag to identify class when outputting to console */
    private static final String TAG = "GameActivity";
    /** String which is used as title of activity when navigation drawer is open */
    private static final String TITLE_DRAWER = "Game Options";

    /** Integer which represents the background color of views in the activity */
    private int COLOR_BACKGROUND;
    /** Integer which represents the highlighted color of views in the activity */
    private int COLOR_HIGHLIGHT;

    /** Byte which represents the default game to load */
    private static final byte DEFAULT_GAME = 0;

    /** Byte which represents the OnClickListener for the frame TextView objects */
    private static final byte LISTENER_TEXT_FRAMES = 0;
    /** Byte which represents the OnClickListener for the pin Button objects */
    private static final byte LISTENER_PIN_BUTTONS = 1;
    /** Byte which represents the OnClickListener for any other objects */
    private static final byte LISTENER_OTHER = 2;

    /** Array of game ids which represent current games that are available to be edited by the user */
    private long[] mGameIds;
    /** Array of frame ids which represent frames of current games that are available to the user */
    private long[] mFrameIds;
    /** The number of games currently available to be edited by the user */
    private byte mNumberOfGames;

    /** The current game which is being edited */
    private byte mCurrentGame = 0;
    /** The current frame which is being edited */
    private byte mCurrentFrame = 0;
    /** The current ball which is being edited */
    private byte mCurrentBall = 0;
    /** Indicates which frames in the game have been accessed */
    private boolean[] mHasFrameBeenAccessed;
    /** Indicates whether the current games being edited belong to an event or not */
    private boolean mEventMode;
    /** True if a certain pin was knocked down after a certain ball in a cerain frame, false otherwise */
    private boolean[][][] mPinState;
    /** True if a foul was invoked on a certain ball in a certain frame */
    private boolean[][] mFouls;
    /** Scores of the current games being edited */
    private short[] mGameScores;
    /** Scores of the current games being edited, with fouls considered */
    private short[] mGameScoresMinusFouls;

    /** Initial title of the activity when it is first created */
    private String mActivityTitle;
    /** List of items which are to be displayed in the navigation drawer */
    private List<String> navigationDrawerOptions;

    /** TextView which displays score gained on a certain ball in a certain frame */
    private TextView[][] mTextViewBallScores;
    /** TextView which indicates whether a foul was invoked on a certain ball in a certain frame */
    private TextView[][] mTextViewFouls;
    /** TextView which displays total score (not considering fouls) in a certain frame */
    private TextView[] mTextViewFrames;
    /** Displays TextView objects in a layout which user can interact with to access specific frames */
    private HorizontalScrollView hsvFrames;
    /** Displays final score of the game, with fouls considered */
    private TextView mTextViewFinalScore;
    /** ImageButton objects which user interacts with to indicate state of the pins in a certain frame */
    private ImageButton[] mImageButtonPins;
    /** ImageView which user interacts with to knock down all pins in a frame */
    private ImageView mImageViewClearPins;

    /** Instance of navigation drawer */
    private DrawerLayout mDrawerLayout;
    /** ListView which displays navigation drawer options */
    private ListView mDrawerList;
    /** Adapts navigation drawer options to be displayed */
    private ArrayAdapter<String> mDrawerAdapter;
    /** Listens for navigation drawer events */
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mActivityTitle = getTitle().toString();
        updateTheme();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        COLOR_BACKGROUND = getResources().getColor(android.R.color.darker_gray);
        COLOR_HIGHLIGHT = getResources().getColor(android.R.color.secondary_text_dark);

        //Requests test ads to be displayed in AdView
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

        /*
         * Creates TextView objects to display information about state of game and
         * stores references in member variables
         */

        /*
         * TODO: should test increasing size of text views by 1dp width and height
         * to see if it removes the thick borders between them
         */
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
                layoutParams = new RelativeLayout.LayoutParams(getPixelsFromDP(40), getPixelsFromDP(41));
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
            textFrameNumber.setTextColor(getResources().getColor(android.R.color.white));
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

        mImageButtonPins = new ImageButton[5];
        for (int i = 0; i < mImageButtonPins.length; i++)
        {
            switch(i)
            {
                case 0: mImageButtonPins[i] = (ImageButton)findViewById(R.id.button_pin_1); break;
                case 1: mImageButtonPins[i] = (ImageButton)findViewById(R.id.button_pin_2); break;
                case 2: mImageButtonPins[i] = (ImageButton)findViewById(R.id.button_pin_3); break;
                case 3: mImageButtonPins[i] = (ImageButton)findViewById(R.id.button_pin_4); break;
                case 4: mImageButtonPins[i] = (ImageButton)findViewById(R.id.button_pin_5); break;
            }
            mImageButtonPins[i].setOnClickListener(onClickListeners[LISTENER_PIN_BUTTONS]);
        }

        findViewById(R.id.imageView_next_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.imageView_prev_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.textView_next_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.textView_prev_ball).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.imageView_foul).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        findViewById(R.id.imageView_reset_frame).setOnClickListener(onClickListeners[LISTENER_OTHER]);
        mImageViewClearPins = (ImageView)findViewById(R.id.imageView_clear_pins);
        mImageViewClearPins.setOnClickListener(onClickListeners[LISTENER_OTHER]);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.game_drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer_games);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mDrawerLayout.closeDrawer(mDrawerList);
                switch(position)
                {
                    case 0:
                        //Returns to the main activity
                        Intent mainIntent = new Intent(GameActivity.this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    case 1:
                        //Returns to the league/event activity
                        Intent leagueIntent = new Intent(GameActivity.this, LeagueEventActivity.class);
                        leagueIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(leagueIntent);
                    case 2:
                        if (!mEventMode)
                        {
                            /*
                             * If the activity is not in event mode, then the app
                             * returns to the series activity.
                             */
                            Intent seriesIntent = new Intent(GameActivity.this, SeriesActivity.class);
                            seriesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(seriesIntent);
                            break;
                        }
                    default:
                        /*
                         * Saves the state of the current game and then loads a new game from
                         * the database to be edited
                         */
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
                        loadGameFromDatabase((byte)(position - (mEventMode ? 2:3)));
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

        if(Theme.getGameActivityThemeInvalidated())
        {
            updateTheme();
        }

        loadInitialScores();
        setScoresInNavigationDrawer();
        loadGameFromDatabase(DEFAULT_GAME);
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
        //TODO menu button enable/disable when added
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
            case R.id.action_what_if:
                showWhatIfDialog();
                return true;
            case R.id.action_stats:
                showGameStats();
                return true;
            case R.id.action_settings:
                showSettingsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new settings activity and displays it to the user
     */
    private void showSettingsMenu()
    {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra(Constants.EXTRA_SETTINGS_SOURCE, TAG);
        startActivity(settingsIntent);
    }

    /**
     * Creates a StatsActivity to displays the stats corresponding to the current game
     */
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

    /**
     * Produces a dialog which informs the user of the best possible score
     * they can get in the current game with the remaining frames
     */
    private void showWhatIfDialog()
    {
        StringBuilder alertMessageBuilder = new StringBuilder("If you get");
        short possibleScore = Short.parseShort(mTextViewFrames[mCurrentFrame].getText().toString());

        if (mCurrentFrame < Constants.LAST_FRAME)
        {
            if (Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
            {
                int firstBallNextFrame = GameScore.getValueOfFrame(mPinState[mCurrentFrame + 1][0]);
                if (firstBallNextFrame == 15 && mCurrentFrame < Constants.LAST_FRAME - 1)
                {
                    possibleScore -= 15;
                    possibleScore -= GameScore.getValueOfFrame(mPinState[mCurrentFrame + 2][0]);
                }
                else
                {
                    possibleScore -= GameScore.getValueOfFrame(mPinState[mCurrentFrame + 1][1]);
                }
            }
            else if (Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN))
            {
                possibleScore -= GameScore.getValueOfFrame(mPinState[mCurrentFrame + 1][0]);
            }
        }

        int pinsLeftStanding = 0;
        for (int i = 0; i < 5; i++)
        {
            if (!mPinState[mCurrentFrame][mCurrentBall][i])
            {
                switch(i)
                {
                    case 0:case 4: pinsLeftStanding += 2; break;
                    case 1:case 3: pinsLeftStanding += 3; break;
                    case 2: pinsLeftStanding += 5; break;
                }
            }
        }

        boolean strikeLastFrame = false;
        boolean strikeTwoFramesAgo = false;
        boolean spareLastFrame = false;

        if (mCurrentFrame > 0)
        {
            if (Arrays.equals(mPinState[mCurrentFrame - 1][0], Constants.FRAME_PINS_DOWN))
            {
                strikeLastFrame = true;
                if (mCurrentFrame > 1 && Arrays.equals(mPinState[mCurrentFrame - 2][0], Constants.FRAME_PINS_DOWN))
                    strikeTwoFramesAgo = true;
            }
            else
            {
                if (Arrays.equals(mPinState[mCurrentFrame - 1][1], Constants.FRAME_PINS_DOWN))
                    spareLastFrame = true;
            }
        }

        if (mCurrentBall == 0)
        {
            alertMessageBuilder.append(" a strike");
            possibleScore += pinsLeftStanding + 30;
            if (strikeLastFrame)
            {
                possibleScore += pinsLeftStanding + 15;
                if (strikeTwoFramesAgo)
                    possibleScore += pinsLeftStanding;
            } else if (spareLastFrame)
                possibleScore += pinsLeftStanding;
        }
        else if (mCurrentBall == 1)
        {
            if (mCurrentFrame == Constants.LAST_FRAME && Arrays.equals(mPinState[mCurrentFrame][0], Constants.FRAME_PINS_DOWN))
                alertMessageBuilder.append(" a strike");
            else
                alertMessageBuilder.append(" a spare");
            possibleScore += pinsLeftStanding + 15;
            if (strikeLastFrame)
                possibleScore += pinsLeftStanding;
        }
        else
        {
            if (mCurrentFrame == Constants.LAST_FRAME && Arrays.equals(mPinState[mCurrentFrame][1], Constants.FRAME_PINS_DOWN))
                alertMessageBuilder.append(" a strike");
            else
                alertMessageBuilder.append(" fifteen");
            possibleScore += pinsLeftStanding;
        }
        possibleScore += 45 * (Constants.LAST_FRAME - mCurrentFrame);

        for (int i = 0; i <= mCurrentFrame; i++)
        {
            for (int j = 0; j < 3 && !(i == mCurrentFrame && j >= mCurrentBall); j++)
            {
                if (mFouls[i][j])
                    possibleScore -= 15;
            }
        }
        if (possibleScore < 0)
            possibleScore = 0;
        alertMessageBuilder.append(" this ball, and strikes onwards, your final score will be ");
        alertMessageBuilder.append(possibleScore);
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setMessage(alertMessageBuilder.toString())
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        builder.create()
                .show();
    }

    /**
     * Creates instances of OnClickListener to listen to events created by
     * views in this activity
     *
     * @return OnClickListener instances which are applied to views in this activity
     */
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
                        //Changes the current frame and updates the GUI
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
                    case R.id.button_pin_5: ballToSet++;
                    case R.id.button_pin_4: ballToSet++;
                    case R.id.button_pin_3: ballToSet++;
                    case R.id.button_pin_2: ballToSet++;
                    case R.id.button_pin_1:
                        alterPinState(ballToSet);
                        break;
                    default:
                        throw new RuntimeException("Invalid pin button id");
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
                    case R.id.imageView_clear_pins:
                        clearPins();
                        break;
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
                        break;
                    case R.id.imageView_next_ball:
                    case R.id.textView_next_ball:
                        //Changes the current frame and updates the GUI
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
                        //Changes the current frame and updates the GUI
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

    /**
     * Sets the text of the three TextView instances which correspond to frameToUpdate
     *
     * @param frameToUpdate frame of which text should be updated
     */
    private void updateBalls(final byte frameToUpdate)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Sets text depending on state of pins in the frame
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

    /**
     * Sets text of the TextView instances which display the score up to the frame to the user
     */
    private void updateScore()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Calculates and keeps running total of scores of each frame
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
                        //Sets scores calculated from running total as text of TextViews
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

    /**
     * Counts fouls of the frames and calculates scores minus 15 points
     * for each foul, then sets score in last TextView
     */
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
                setScoresInNavigationDrawer();
            }
        });
    }

    /**
     * Sets background color of current ball and frame TextView instances to COLOR_BACKGROUND
     */
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

    /**
     * Sets background color of current ball and frame TextView instances to COLOR_HIGHLIGHT
     * and sets color of pin and whether its enabled or not depending on its state
     */
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

                int numberOfPinsStanding = 0;
                for (int i = 0; i < 5; i++)
                {
                    if (mPinState[mCurrentFrame][mCurrentBall][i])
                    {
                        mImageButtonPins[i].setImageResource(R.drawable.pin_gray);
                        numberOfPinsStanding++;
                    }
                    else
                    {
                        mImageButtonPins[i].setImageResource(R.drawable.pin);
                    }

                    if (mCurrentBall > 0 && (mPinState[mCurrentFrame][mCurrentBall - 1][i])
                            && !(mCurrentFrame == Constants.LAST_FRAME
                            && Arrays.equals(mPinState[mCurrentFrame][mCurrentBall - 1], Constants.FRAME_PINS_DOWN)))
                    {
                        mImageButtonPins[i].setEnabled(false);
                    }
                    else
                    {
                        mImageButtonPins[i].setEnabled(true);
                    }
                }

                switch(mCurrentBall)
                {
                    case 0:mImageViewClearPins.setImageResource(R.drawable.ic_action_strike);break;
                    case 1:mImageViewClearPins.setImageResource(R.drawable.ic_action_spare);break;
                    case 2:mImageViewClearPins.setImageResource(R.drawable.ic_action_fifteen);break;
                }
                mImageViewClearPins.setEnabled(numberOfPinsStanding < 5);

                focusOnFrame();
            }
        });
    }

    /**
     * Saves a games score and individual frames to the database on a separate thread.
     *
     * @param srcActivity activity which called the method to get instance of database
     * @param gameId id of the game to be updated
     * @param frameIds ids of the frames to be updated
     * @param hasFrameBeenAccessed state of whether frames have been accessed or not
     * @param pinState state of pins after each ball
     * @param fouls indicates whether a foul was invoked on each ball
     * @param finalScore final score of the game, considering fouls
     */
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

    /**
     * Loads a game from the database to member variables
     *
     * @param newGame index of id in mGameIds to load
     */
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

                for (byte i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
                    updateBalls(i);
                mHasFrameBeenAccessed[0] = true;
                updateFrameColor();
            }
        }).start();
    }

    /**
     * Scrolls the position of hsvFrames so the current frame is centred
     */
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
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                byte startingPosition = (byte)(mEventMode ? 2:3);
                byte currentGamePosition = startingPosition;
                while (currentGamePosition - startingPosition < mNumberOfGames)
                {
                    navigationDrawerOptions.set(currentGamePosition, "Game "
                            + (currentGamePosition - startingPosition + 1)
                            + " (" + mGameScoresMinusFouls[currentGamePosition - startingPosition] + ")");
                    currentGamePosition++;
                }
            }
        });
    }

    private void loadInitialScores()
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

        int currentGamePosition = 0;
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));
                mGameScoresMinusFouls[currentGamePosition++] = gameScore;
                cursor.moveToNext();
            }
        }
        else
        {
            throw new RuntimeException("No games found - cannot set scores");
        }
    }

    /**
     * Either sets a pin to be standing or knocked down, and updates the score accordingly
     *
     * @param pinToSet the pin which was altered
     */
    private void alterPinState(final byte pinToSet)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final boolean isPinKnockedOver = mPinState[mCurrentFrame][mCurrentBall][pinToSet];
                final boolean allPinsKnockedOver;
                if (!isPinKnockedOver)
                {
                    for (int i = mCurrentBall; i < 3; i++)
                    {
                        mPinState[mCurrentFrame][i][pinToSet] = true;
                    }
                    if (Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN))
                    {
                        for (int i = mCurrentBall + 1; i < 3; i++)
                        {
                            mFouls[mCurrentFrame][i] = false;
                        }
                        if (mCurrentFrame == Constants.LAST_FRAME)
                        {
                            if (mCurrentBall < 2)
                            {
                                for (int j = mCurrentBall + 1; j < 3; j++)
                                {
                                    for (int i = 0; i < 5; i++)
                                    {
                                        mPinState[mCurrentFrame][j][i] = false;
                                    }
                                }
                            }
                        }
                        allPinsKnockedOver = true;
                    }
                    else
                    {
                        allPinsKnockedOver = false;
                    }
                }
                else
                {
                    allPinsKnockedOver = false;
                    for (int i = mCurrentBall; i < 3; i++)
                    {
                        mPinState[mCurrentFrame][i][pinToSet] = false;
                    }
                    if (mCurrentFrame == Constants.LAST_FRAME && mCurrentBall == 1)
                    {
                        System.arraycopy(mPinState[mCurrentFrame][1], 0, mPinState[mCurrentFrame][2], 0, mPinState[mCurrentFrame][1].length);
                    }
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mPinState[mCurrentFrame][mCurrentBall][pinToSet])
                        {
                            mImageButtonPins[pinToSet].setImageResource(R.drawable.pin_gray);
                        }
                        else
                        {
                            mImageButtonPins[pinToSet].setImageResource(R.drawable.pin);
                        }
                        mImageViewClearPins.setEnabled(allPinsKnockedOver);
                    }
                });

                updateBalls(mCurrentFrame);
                updateScore();
            }
        }).start();
    }

    /**
     * Clears all the pins which are currently standing in the frame and updates
     * the TextViews with new score
     */
    private void clearPins()
    {
        if (!Arrays.equals(mPinState[mCurrentFrame][mCurrentBall], Constants.FRAME_PINS_DOWN))
        {
            for (int j = mCurrentBall; j < 3; j++)
            {
                for (int i = 0; i < 5; i++)
                {
                    if (mCurrentFrame == Constants.LAST_FRAME)
                    {
                        mPinState[mCurrentFrame][j][i] = (j == mCurrentBall);
                    }
                    else
                    {
                        mPinState[mCurrentFrame][j][i] = true;
                    }
                    if (j > mCurrentBall)
                        mFouls[mCurrentFrame][j] = false;
                }
            }

            Log.w(TAG, "Cleared");
            updateBalls(mCurrentFrame);
            updateScore();
            updateFrameColor();
        }
    }

    /**
     * Converts a dp value to pixels
     * @param dps value to be converted
     * @return result of conversion from dps to pixels
     */
    private int getPixelsFromDP(int dps)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dps * scale + 0.5f);
    }

    @Override
    public void updateTheme()
    {
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getActionBarThemeColor()));
        findViewById(R.id.relativeLayout_game_toolbar)
                .setBackgroundColor(Theme.getActionBarTabThemeColor());
        Theme.validateGameActivityTheme();
    }
}

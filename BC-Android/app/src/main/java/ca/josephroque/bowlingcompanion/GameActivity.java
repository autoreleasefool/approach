package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import at.markushi.ui.CircleButton;
import ca.josephroque.bowlingcompanion.data.FileCreator;
import ca.josephroque.bowlingcompanion.data.GameScore;
import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-01-09.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */

public class GameActivity extends ActionBarActivity implements View.OnClickListener
{

    /** TAG identifier for output to log */
    private static final String TAG = "GameActivity";
    /** Title for when navigation drawer is opened */
    private static final String TITLE_DRAWER = "Game Options";
    /** Color of button when the relevant pin is knocked over */
    private static final String COLOR_PIN_KNOCKED = "#000000";
    /** Color of button when the relevant pin is standing */
    private static final String COLOR_PIN_STANDING = "#99CC00";

    /** IDs of all the games being input */
    private long[] gameID = null;
    /** IDs of all the frames being input */
    private long[] frameID = null;
    /** The number of games in the series */
    private int numberOfGames = -1;

    /** Current game being edited (0 - Constants.MAX_NUMBER_OF_GAMES) */
    private int currentGame = 0;
    /** Current frame being edited (0 - 9) */
    private int currentFrame = 0;
    /** Current ball being edited (0 - 2) */
    private int currentBall = 0;
    /** Indicates whether a frame has been previously accessed */
    private boolean[] hasFrameBeenAccessed = null;
    /** Indicates whether tournament mode is active or not */
    private boolean tournamentMode = false;

    /** TextViews showing score of ball thrown */
    private TextView[][] ballsTextViews = null;
    /** TextViews showing fouls */
    private TextView[][] foulsTextViews = null;
    /** TextViews showing score of frame */
    private TextView[] framesTextViews = null;
    /** CircleButtons which manipulate pins */
    private CircleButton[] pinButtons = null;
    /** List of arrays representing state of pins */
    private boolean[][][] balls = null;
    /** List of arrays indicating whether a foul was made */
    private boolean[][] fouls = null;
    /** Scores of current games */
    private int[] gameScores = null;
    /** Scores of current games, considering fouls */
    private int[] gameScoresWithFouls = null;
    /** Title of the activity when loaded */
    private String activityTitle = null;
    /** Indicates whether the overlay has been dismissed */
    private boolean topLevelLayoutDismissed = true;

    /** HorizontalScrollView displaying score tables */
    private HorizontalScrollView hsvFrames = null;
    /** TextView to display final score after considering fouls */
    private TextView textViewFinalScore = null;
    /** Instance of navigation drawer */
    private DrawerLayout drawerLayout = null;
    /** ListView for navigation drawer */
    private ListView drawerList = null;
    /** Listener for navigation drawer open and close actions */
    private ActionBarDrawerToggle drawerToggle = null;
    /** Layout which shows the tutorial first time */
    private RelativeLayout topLevelLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        activityTitle = getTitle().toString();

        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        numberOfGames = preferences.getInt(Constants.PREFERENCES_NUMBER_OF_GAMES, -1);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params;
        hsvFrames = (HorizontalScrollView)findViewById(R.id.hsv_frames);
        ballsTextViews = new TextView[Constants.NUMBER_OF_FRAMES][3];
        foulsTextViews = new TextView[Constants.NUMBER_OF_FRAMES][3];
        framesTextViews = new TextView[Constants.NUMBER_OF_FRAMES];
        balls = new boolean[Constants.NUMBER_OF_FRAMES][3][5];
        fouls = new boolean[Constants.NUMBER_OF_FRAMES][3];
        hasFrameBeenAccessed = new boolean[10];
        gameScores = new int[numberOfGames];
        gameScoresWithFouls = new int[numberOfGames];

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
            frameText.setText("0");
            frameText.setBackgroundResource(R.drawable.text_frame_background);
            frameText.setGravity(Gravity.CENTER);
            frameText.setOnClickListener(this);
            params = new RelativeLayout.LayoutParams(getPixelsFromDP(120), getPixelsFromDP(88));
            params.leftMargin = getPixelsFromDP(120 * i);
            params.topMargin = getPixelsFromDP(40);
            relativeLayout.addView(frameText, params);
            framesTextViews[i] = frameText;

            for (int j = 0; j < 3; j++)
            {
                TextView text = new TextView(this);
                text.setBackgroundResource(R.drawable.text_frame_background);
                text.setGravity(Gravity.CENTER);
                params = new RelativeLayout.LayoutParams(getPixelsFromDP(40), getPixelsFromDP(40));
                params.leftMargin = getPixelsFromDP(120 * i + j * 40);
                params.topMargin = 0;
                relativeLayout.addView(text, params);
                ballsTextViews[i][j] = text;

                text = new TextView(this);
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                params = new RelativeLayout.LayoutParams(getPixelsFromDP(40), getPixelsFromDP(20));
                params.leftMargin = getPixelsFromDP(120 * i + j * 40);
                params.topMargin = getPixelsFromDP(40);
                relativeLayout.addView(text, params);
                foulsTextViews[i][j] = text;
            }

            TextView textView = new TextView(this);
            textView.setText(String.valueOf(i + 1));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            params = new RelativeLayout.LayoutParams(getPixelsFromDP(120), getPixelsFromDP(36));
            params.leftMargin = getPixelsFromDP(120 * i);
            params.topMargin = getPixelsFromDP(128);
            relativeLayout.addView(textView, params);
        }
        textViewFinalScore = new TextView(this);
        textViewFinalScore.setText("0");
        textViewFinalScore.setGravity(Gravity.CENTER);
        textViewFinalScore.setBackgroundResource(R.drawable.text_frame_background);
        params = new RelativeLayout.LayoutParams(getPixelsFromDP(120), getPixelsFromDP(128));
        params.leftMargin = getPixelsFromDP(Constants.NUMBER_OF_FRAMES * 120);
        params.topMargin = 0;
        relativeLayout.addView(textViewFinalScore, params);
        hsvFrames.addView(relativeLayout);

        pinButtons = new CircleButton[5];
        for (int i = 0; i < 5; i++)
        {
            CircleButton circleButton = null;
            switch(i)
            {
                case 0: circleButton = (CircleButton)findViewById(R.id.button_pin_0); break;
                case 1: circleButton = (CircleButton)findViewById(R.id.button_pin_1); break;
                case 2: circleButton = (CircleButton)findViewById(R.id.button_pin_2); break;
                case 3: circleButton = (CircleButton)findViewById(R.id.button_pin_3); break;
                case 4: circleButton = (CircleButton)findViewById(R.id.button_pin_4); break;
            }
            pinButtons[i] = circleButton;
            circleButton.setOnClickListener(this);
        }

        findViewById(R.id.button_next_frame).setOnClickListener(this);
        findViewById(R.id.button_prev_frame).setOnClickListener(this);
        findViewById(R.id.button_foul).setOnClickListener(this);
        findViewById(R.id.button_reset).setOnClickListener(this);
        findViewById(R.id.button_whatif).setOnClickListener(this);

        GradientDrawable drawable = (GradientDrawable)framesTextViews[currentFrame].getBackground();
        drawable.setColor(Color.RED);
        drawable = (GradientDrawable)ballsTextViews[currentFrame][currentBall].getBackground();
        drawable.setColor(Color.RED);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer_games);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    switch(position)
                    {
                        case 0:
                            drawerLayout.closeDrawer(drawerList);
                            Intent mainIntent = new Intent(GameActivity.this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            break;
                        case 1:
                            drawerLayout.closeDrawer(drawerList);
                            Intent leagueIntent = new Intent(GameActivity.this, LeagueActivity.class);
                            leagueIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(leagueIntent);
                            break;
                        case 2:
                            if (!tournamentMode)
                            {
                                drawerLayout.closeDrawer(drawerList);
                                Intent seriesIntent = new Intent(GameActivity.this, SeriesActivity.class);
                                seriesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(seriesIntent);
                                break;
                            }
                        default:
                            saveGameToDatabase(true);
                            loadGameFromDatabase(position - (tournamentMode ? 2:3));
                            drawerLayout.closeDrawer(drawerList);
                            break;
                    }
                }
            });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
            {
                public void onDrawerClosed(View view)
                {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(activityTitle);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView)
                {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle(TITLE_DRAWER);
                    invalidateOptionsMenu();
                }
            };
        drawerLayout.setDrawerListener(drawerToggle);

        topLevelLayout = (RelativeLayout)findViewById(R.id.game_top_layout);
        if (hasShownTutorial())
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_game_stats).setVisible(!drawerOpen);
        menu.findItem(R.id.action_game_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        numberOfGames = preferences.getInt(Constants.PREFERENCES_NUMBER_OF_GAMES, -1);
        tournamentMode = preferences.getBoolean(Constants.PREFERENCES_TOURNAMENT_MODE, false);

        Intent intent = getIntent();
        gameID = intent.getLongArrayExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID);
        frameID = intent.getLongArrayExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID);

        int extraOptions = (tournamentMode) ? 2:3;
        String[] gameTitles = new String[numberOfGames + extraOptions];
        gameTitles[0] = "Bowler";
        gameTitles[1] = "Leagues";
        if (!tournamentMode)
        {
            gameTitles[2] = "Series";
        }
        for (int i = extraOptions; i < gameTitles.length; i++)
        {
            gameTitles[i] = "Game " + (i + 1 - extraOptions);
        }
        drawerList = (ListView)findViewById(R.id.left_drawer_games);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.text_games_list, gameTitles));

        loadGameFromDatabase(0);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        clearFrameColor();
        saveGameToDatabase(true);
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
        if (topLevelLayout.getVisibility() == View.VISIBLE)
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        switch(id)
        {
            case R.id.action_game_stats:
                showGameStats();
                return true;
            case R.id.action_game_share:
                showShareOptions();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new StatsActivity to display the statistics
     * relevant to the current displayed game
     */
    private void showGameStats()
    {
        clearFrameColor();
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_GAME, gameID[currentGame])
                .putInt(Constants.PREFERENCES_GAME_NUMBER, currentGame + 1)
                .apply();

        //saveGameToDatabase(false);
        Intent statsIntent = new Intent(GameActivity.this, StatsActivity.class);
        startActivity(statsIntent);
    }

    /**
     * Converts a value in DP to pixels
     *
     * @param dps value in dp
     * @return value of dps in pixels relative to device
     */
    private int getPixelsFromDP(int dps)
    {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dps * scale + 0.5f);
    }

    @Override
    public void onClick(View view)
    {
        if (!topLevelLayoutDismissed)
        {
            return;
        }

        int frameToSet = 0;
        int ballToSet = 0;

        switch(view.getId())
        {
            case R.id.button_reset:
                clearFrameColor();
                for (int i = 0; i < 3; i++)
                {
                    fouls[currentFrame][i] = false;
                    currentBall = 0;
                    for (int j = 0; j < 5; j++)
                        balls[currentFrame][i][j] = false;
                }
                updateBalls(currentFrame);
                updateScore();
                updateFouls();
                updateFrameColor();
                break;
            case R.id.button_whatif:
                int possibleScore = (currentFrame > 0)
                        ? Integer.parseInt(framesTextViews[currentFrame - 1].getText().toString())
                        : 0;

                boolean spareLastFrame = false;
                boolean strikeLastFrame = false;
                boolean strikeTwoFramesAgo = false;
                if (currentFrame > 1 && Arrays.equals(balls[currentFrame - 1][1], Constants.FRAME_CLEAR))
                {
                    if (Arrays.equals(balls[currentFrame - 1][0], Constants.FRAME_CLEAR))
                    {
                        strikeLastFrame = true;
                        if (currentFrame > 2 && Arrays.equals(balls[currentFrame - 2][0], Constants.FRAME_CLEAR))
                            strikeTwoFramesAgo = true;
                    }
                    else
                    {
                        spareLastFrame = true;
                    }
                }

                StringBuilder alertMessageBuilder = new StringBuilder("If you get ");
                if (currentBall == 0)
                {
                    alertMessageBuilder.append("a strike ");
                    possibleScore += 45;
                    if (spareLastFrame)
                    {
                        possibleScore += 15;
                    }
                    else if (strikeLastFrame)
                    {
                        possibleScore += 30;
                        if (strikeTwoFramesAgo)
                        {
                            possibleScore += 15;
                        }
                    }
                }
                else if (currentBall == 1)
                {
                    alertMessageBuilder.append("a spare ");
                    possibleScore += 30;
                    int firstBall = GameScore.getValueOfFrame(balls[currentFrame][0]);
                    if (spareLastFrame)
                    {
                        possibleScore += firstBall;
                    }
                    else if (strikeLastFrame)
                    {
                        possibleScore += 15;
                        if (strikeTwoFramesAgo)
                        {
                            possibleScore += firstBall;
                        }
                    }
                }
                else
                {
                    alertMessageBuilder.append("fifteen ");
                    possibleScore += 15;
                    int firstBall = GameScore.getValueOfFrame(balls[currentFrame][0]);
                    int secondBall = GameScore.getValueOfFrameDifference(balls[currentFrame][0], balls[currentFrame][1]);
                    if (spareLastFrame)
                    {
                        possibleScore += firstBall;
                    }
                    else if (strikeLastFrame)
                    {
                        possibleScore += firstBall + secondBall;
                        if (strikeTwoFramesAgo)
                        {
                            possibleScore += firstBall;
                        }
                    }
                }

                for (int i = currentFrame + 1; i < Constants.NUMBER_OF_FRAMES; i++)
                {
                    possibleScore += 45;
                }
                for (int i = 0; i <= currentFrame; i++)
                {
                    for (int j = 0; j < 3 && !(i == currentFrame && j >= currentBall); j++)
                    {
                        if (fouls[i][j])
                            possibleScore -= 15;
                    }
                }
                if (possibleScore < 0)
                    possibleScore = 0;
                alertMessageBuilder.append(" this frame, and strikes onwards, your final score will be ");
                alertMessageBuilder.append(possibleScore);
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setMessage(alertMessageBuilder.toString())
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //do nothing
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.button_foul:
                fouls[currentFrame][currentBall] = !fouls[currentFrame][currentBall];
                foulsTextViews[currentFrame][currentBall]
                        .setText(fouls[currentFrame][currentBall]
                        ? "F"
                        : "");
                updateFouls();
                break;
            case R.id.button_next_frame:
                //Clears the coloring of the current frame, increases the ball and/or
                //frame if possible, then recolors the new frame/ball
                clearFrameColor();
                if (Arrays.equals(balls[currentFrame][currentBall], Constants.FRAME_CLEAR))
                {
                    if (currentFrame < 9)
                    {
                        currentBall = 0;
                        currentFrame++;
                    }
                    else if (currentBall < 2)
                    {
                        currentBall++;
                    }
                }
                else if (++currentBall == 3)
                {
                    currentBall = 0;
                    if (++currentFrame == 10)
                    {
                        currentFrame = 9;
                        currentBall = 2;
                    }
                }
                for (int i = currentFrame; i >= 0; i--)
                {
                    if (hasFrameBeenAccessed[currentFrame])
                        break;
                    hasFrameBeenAccessed[currentFrame] = true;
                }
                updateFrameColor();
                break;
            case R.id.button_prev_frame:
                //Clears the coloring of the current frame, decreases the ball and/or
                //frame if possible, then recolors the new frame/ball
                clearFrameColor();
                if (--currentBall == -1)
                {
                    if (--currentFrame == -1)
                    {
                        currentFrame = 0;
                        currentBall = 0;
                    }
                    else
                    {
                        currentBall = 0;
                        while (!Arrays.equals(balls[currentFrame][currentBall], Constants.FRAME_CLEAR) && currentBall < 2)
                        {
                            currentBall++;
                        }
                    }
                }
                updateFrameColor();
                break;
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
                //When user selects a frame textview in the horizontal scroll view
                clearFrameColor();
                currentFrame = frameToSet;
                currentBall = 0;
                for (int i = currentFrame; i >= 0; i--)
                {
                    if (hasFrameBeenAccessed[currentFrame])
                        break;
                    hasFrameBeenAccessed[currentFrame] = true;
                }
                updateFrameColor();
                break;
            case R.id.button_pin_4: ballToSet++;
            case R.id.button_pin_3: ballToSet++;
            case R.id.button_pin_2: ballToSet++;
            case R.id.button_pin_1: ballToSet++;
            case R.id.button_pin_0:

                //When user selects a pin button
                boolean isPinKnockedOver = balls[currentFrame][currentBall][ballToSet];
                if (!isPinKnockedOver)
                {
                    //pin was standing
                    pinButtons[ballToSet].setColor(Color.parseColor(COLOR_PIN_KNOCKED));
                    for (int i = currentBall; i < 3; i++)
                    {
                        balls[currentFrame][i][ballToSet] = true;
                    }
                    if (Arrays.equals(balls[currentFrame][currentBall], Constants.FRAME_CLEAR))
                    {
                        for (int i = currentBall + 1; i < 3; i++)
                        {
                            fouls[currentFrame][i] = false;
                        }
                        clearFrameColor();
                        if (currentFrame == Constants.LAST_FRAME)
                        {
                            if (currentBall < 2)
                            {
                                for (int j = currentBall + 1; j < 3; j++)
                                {
                                    for (int i = 0; i < 5; i++)
                                    {
                                        balls[currentFrame][j][i] = false;
                                    }
                                }
                            }
                        }
                        updateFrameColor();
                    }
                }
                else
                {
                    //pin was down
                    pinButtons[ballToSet].setColor(Color.parseColor(COLOR_PIN_STANDING));
                    for (int i = currentBall; i < 3; i++)
                    {
                        balls[currentFrame][i][ballToSet] = false;
                    }
                    if (currentFrame == Constants.LAST_FRAME && currentBall == 1)
                    {
                        System.arraycopy(balls[currentFrame][1], 0, balls[currentFrame][2], 0, balls[currentFrame][1].length);
                    }
                }
                updateBalls(currentFrame);
                updateScore();
                break;
            default:
                throw new RuntimeException("GameActivity#onClick unknown button ID");
        }
    }

    /**
     * Sets the TextView displaying score corresponding to the current frame
     * to the textual value of the ball from getValueOfBall
     */
    private void updateBalls(int frameToUpdate)
    {
        if (frameToUpdate == Constants.LAST_FRAME)
        {
            if (Arrays.equals(balls[frameToUpdate][0], Constants.FRAME_CLEAR))
            {
                ballsTextViews[frameToUpdate][0].setText(Constants.BALL_STRIKE);
                if (Arrays.equals(balls[frameToUpdate][1], Constants.FRAME_CLEAR))
                {
                    ballsTextViews[frameToUpdate][1].setText(Constants.BALL_STRIKE);
                    ballsTextViews[frameToUpdate][2].setText(GameScore.getValueOfBall(balls[frameToUpdate][2], 2, true));
                }
                else
                {
                    ballsTextViews[frameToUpdate][1].setText(GameScore.getValueOfBall(balls[frameToUpdate][1], 1, false));
                    if (Arrays.equals(balls[frameToUpdate][2], Constants.FRAME_CLEAR))
                        ballsTextViews[frameToUpdate][2].setText(Constants.BALL_SPARE);
                    else
                        ballsTextViews[frameToUpdate][2].setText(GameScore.getValueOfBallDifference(balls[frameToUpdate], 2, false));
                }
            }
            else
            {
                ballsTextViews[frameToUpdate][0].setText(GameScore.getValueOfBall(balls[frameToUpdate][0], 0, false));
                if (Arrays.equals(balls[frameToUpdate][1], Constants.FRAME_CLEAR))
                {
                    ballsTextViews[frameToUpdate][1].setText(Constants.BALL_SPARE);
                    ballsTextViews[frameToUpdate][2].setText(GameScore.getValueOfBall(balls[frameToUpdate][2], 2, true));
                }
                else
                {
                    ballsTextViews[frameToUpdate][1].setText(GameScore.getValueOfBallDifference(balls[frameToUpdate], 1, false));
                    ballsTextViews[frameToUpdate][2].setText(GameScore.getValueOfBallDifference(balls[frameToUpdate], 2, false));
                }
            }
        }
        else
        {
            ballsTextViews[frameToUpdate][0].setText(GameScore.getValueOfBallDifference(balls[frameToUpdate], 0, false));
            if (!Arrays.equals(balls[frameToUpdate][0], Constants.FRAME_CLEAR))
            {
                if (Arrays.equals(balls[frameToUpdate][1], Constants.FRAME_CLEAR))
                {
                    ballsTextViews[frameToUpdate][1].setText(Constants.BALL_SPARE);
                    ballsTextViews[frameToUpdate][2].setText(Constants.BALL_EMPTY);
                }
                else
                {
                    ballsTextViews[frameToUpdate][1].setText(GameScore.getValueOfBallDifference(balls[frameToUpdate], 1, false));
                    ballsTextViews[frameToUpdate][2].setText(GameScore.getValueOfBallDifference(balls[frameToUpdate], 2, false));
                }
            }
            else
            {
                ballsTextViews[frameToUpdate][1].setText(Constants.BALL_EMPTY);
                ballsTextViews[frameToUpdate][2].setText(Constants.BALL_EMPTY);
            }
        }

        for (int i = 0; i < 3; i++)
        {
            foulsTextViews[frameToUpdate][i].setText(
                    (fouls[frameToUpdate][i])
                    ? "F"
                    : "");
        }
    }

    /**
     * Updates the score values of all frames
     */
    private void updateScore()
    {
        int[] frameScores = new int[10];
        for (int f = Constants.LAST_FRAME; f >= 0; f--)
        {
            if (f == Constants.LAST_FRAME)
            {
                for (int b = 2; b >= 0; b--)
                {
                    switch(b)
                    {
                        case 2:
                            frameScores[f] += GameScore.getValueOfFrame(balls[f][b]);
                            break;
                        case 1:
                        case 0:
                            if (Arrays.equals(balls[f][b], Constants.FRAME_CLEAR))
                            {
                                frameScores[f] += GameScore.getValueOfFrame(balls[f][b]);
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
                    if (b < 2 && Arrays.equals(balls[f][b], Constants.FRAME_CLEAR))
                    {
                        frameScores[f] += GameScore.getValueOfFrame(balls[f][b]);
                        frameScores[f] += GameScore.getValueOfFrame(balls[f + 1][0]);
                        if (b == 0)
                        {
                            if (f == Constants.LAST_FRAME - 1)
                            {
                                if (frameScores[f] == 30)
                                {
                                    frameScores[f] += GameScore.getValueOfFrame(balls[f + 1][1]);
                                }
                                else
                                {
                                    frameScores[f] += GameScore.getValueOfFrameDifference(balls[f + 1][0], balls[f + 1][1]);
                                }
                            }
                            else if (frameScores[f] < 30)
                            {
                                frameScores[f] += GameScore.getValueOfFrameDifference(balls[f + 1][0], balls[f + 1][1]);
                            }
                            else
                            {
                                frameScores[f] += GameScore.getValueOfFrame(balls[f + 2][0]);
                            }
                        }
                        break;
                    }
                    else if (b == 2)
                    {
                        frameScores[f] += GameScore.getValueOfFrame(balls[f][b]);
                    }
                }
            }
        }

        int totalScore = 0;
        for (int i = 0; i < frameScores.length; i++)
        {
            totalScore += frameScores[i];
            framesTextViews[i].setText(String.valueOf(totalScore));
        }
        gameScores[currentGame] = totalScore;
        updateFouls();
    }

    /**
     * Updates the score after considering fouls
     */
    private void updateFouls()
    {
        int foulCount = 0;
        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (fouls[i][j])
                    foulCount++;
            }
        }
        int scoreWithFouls = gameScores[currentGame] - 15 * foulCount;
        if (scoreWithFouls < 0)
            scoreWithFouls = 0;
        gameScoresWithFouls[currentGame] = scoreWithFouls;
        textViewFinalScore.setText(String.valueOf(gameScoresWithFouls[currentGame]));
    }

    /**
     * Sets the background color of the current frame to white
     */
    private void clearFrameColor()
    {
        GradientDrawable drawable = (GradientDrawable) ballsTextViews[currentFrame][currentBall].getBackground();
        drawable.setColor(Color.WHITE);
        drawable = (GradientDrawable) framesTextViews[currentFrame].getBackground();
        drawable.setColor(Color.WHITE);
    }

    /**
     * Sets the background color of the current frame to red
     */
    private void updateFrameColor()
    {
        GradientDrawable drawable = (GradientDrawable) ballsTextViews[currentFrame][currentBall].getBackground();
        drawable.setColor(Color.RED);
        drawable = (GradientDrawable) framesTextViews[currentFrame].getBackground();
        drawable.setColor(Color.RED);

        for (int i = 0; i < 5; i++)
        {
            CircleButton button = null;
            switch(i)
            {
                case 0: button = (CircleButton)findViewById(R.id.button_pin_0); break;
                case 1: button = (CircleButton)findViewById(R.id.button_pin_1); break;
                case 2: button = (CircleButton)findViewById(R.id.button_pin_2); break;
                case 3: button = (CircleButton)findViewById(R.id.button_pin_3); break;
                case 4: button = (CircleButton)findViewById(R.id.button_pin_4); break;
            }
            if (balls[currentFrame][currentBall][i])
            {
                button.setColor(Color.parseColor(COLOR_PIN_KNOCKED));
            }
            else
            {
                button.setColor(Color.parseColor(COLOR_PIN_STANDING));
            }

            if (currentBall > 0 && (balls[currentFrame][currentBall - 1][i])
                    && !(currentFrame == Constants.LAST_FRAME
                    && Arrays.equals(balls[currentFrame][currentBall - 1], Constants.FRAME_CLEAR)))
            {
                button.setEnabled(false);
            }
            else
            {
                button.setEnabled(true);
            }
        }
        focusOnFrame();
    }

    /**
     * Saves the game to the database
     */
    private void saveGameToDatabase()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();
        ContentValues values;

        database.beginTransaction();
        try
        {
            values = new ContentValues();
            values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, gameScoresWithFouls[currentGame]);
            database.update(GameEntry.TABLE_NAME,
                    values,
                    GameEntry._ID + "=?",
                    new String[]{String.valueOf(gameID[currentGame])});

            for (int i = 0; i < 10; i++)
            {
                StringBuilder foulsOfFrame = new StringBuilder();
                for (int ballCounter = 0; ballCounter < 3; ballCounter++)
                {
                    if (fouls[i][ballCounter])
                    {
                        foulsOfFrame.append(ballCounter + 1);
                    }
                }
                if (foulsOfFrame.length() == 0)
                    foulsOfFrame.append(0);

                values = new ContentValues();
                values.put(FrameEntry.COLUMN_NAME_BALL[0], booleanFrameToString(balls[i][0]));
                values.put(FrameEntry.COLUMN_NAME_BALL[1], booleanFrameToString(balls[i][1]));
                values.put(FrameEntry.COLUMN_NAME_BALL[2], booleanFrameToString(balls[i][2]));
                values.put(FrameEntry.COLUMN_NAME_FRAME_ACCESSED, (hasFrameBeenAccessed[i]) ? 1:0);
                values.put(FrameEntry.COLUMN_NAME_FOULS, foulsOfFrame.toString());
                database.update(FrameEntry.TABLE_NAME,
                        values,
                        FrameEntry._ID + "=?",
                        new String[]{String.valueOf(frameID[currentGame * 10 + i])});
            }

            database.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w(TAG, "Error saving game " + currentGame);
        }
        finally
        {
            database.endTransaction();
        }
    }

    /**
     * Loads a game from the database and displays it in the textviews
     *
     * @param newGame game number to load
     */
    private void loadGameFromDatabase(int newGame)
    {
        clearFrameColor();
        currentGame = newGame;
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();

        Cursor cursor = database.query(FrameEntry.TABLE_NAME,
                new String[]{FrameEntry.COLUMN_NAME_FRAME_ACCESSED, FrameEntry.COLUMN_NAME_BALL[0], FrameEntry.COLUMN_NAME_BALL[1], FrameEntry.COLUMN_NAME_BALL[2], FrameEntry.COLUMN_NAME_FOULS},
                FrameEntry.COLUMN_NAME_GAME_ID + "=?",
                new String[]{String.valueOf(gameID[currentGame])},
                null,
                null,
                FrameEntry.COLUMN_NAME_FRAME_NUMBER);

        fouls = new boolean[Constants.NUMBER_OF_FRAMES][3];
        int currentFrameIterator = 0;
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                int frameAccessed = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED));
                hasFrameBeenAccessed[currentFrameIterator] = (frameAccessed == 1);
                for (int i = 0; i < 3; i++)
                {
                    String ballString = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[i]));
                    boolean[] ballBoolean = {GameScore.getBoolean(ballString.charAt(0)), GameScore.getBoolean(ballString.charAt(1)), GameScore.getBoolean(ballString.charAt(2)), GameScore.getBoolean(ballString.charAt(3)), GameScore.getBoolean(ballString.charAt(4))};
                    balls[currentFrameIterator][i] = ballBoolean;
                }
                String foulsOfFrame = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                for (int ballCounter = 0; ballCounter < 3; ballCounter++)
                {
                    if (foulsOfFrame.contains(String.valueOf(ballCounter + 1)))
                    {
                        fouls[currentFrameIterator][ballCounter] = true;
                    }
                }

                currentFrameIterator++;
                cursor.moveToNext();
            }
        }
        currentFrame = 0;
        currentBall = 0;
        updateScore();

        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            updateBalls(i);
        }
        hasFrameBeenAccessed[0] = true;
        updateFrameColor();
    }

    /**
     * Smooths scrolls horizontal scroll view to the current frame
     */
    private void focusOnFrame()
    {
        hsvFrames.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (currentFrame >= 1)
                    {
                        hsvFrames.smoothScrollTo(framesTextViews[currentFrame - 1].getLeft(), 0);
                    }
                    else
                    {
                        hsvFrames.smoothScrollTo(framesTextViews[currentFrame].getLeft(), 0);
                    }
                }
            });
    }

    /**
     * Creates a string from an array of booleans
     *
     * @param frame array to convert to string
     * @return A String of 1's and 0's, where a 1 represents true in the array
     */
    private String booleanFrameToString(boolean[] frame)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (boolean b : frame)
        {
            stringBuilder.append(b ? 1:0);
        }
        return stringBuilder.toString();
    }

    /**
     * Displays a tutorial overlay if one hasn't been shown to
     * the user yet
     *
     * @return true if the tutorial has already been shown, false otherwise
     */
    private boolean hasShownTutorial()
    {
        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        boolean hasShownTutorial = preferences.getBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_GAME, false);

        if (!hasShownTutorial)
        {
            topLevelLayoutDismissed = false;
            preferences.edit()
                    .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_GAME, true)
                    .apply();
            topLevelLayout.setVisibility(View.VISIBLE);
            topLevelLayout.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    topLevelLayout.setVisibility(View.INVISIBLE);

                    Timer dismissTimer = new Timer();
                    dismissTimer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            topLevelLayoutDismissed = true;
                        }
                    }, 100);

                    return false;
                }
            });
        }
        return hasShownTutorial;
    }

    /**
     * Shows options relevant to sharing game data to social media
     */
    private void showShareOptions()
    {
    }
}

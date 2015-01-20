package ca.josephroque.bowlingcompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
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

    /** TextViews showing score of ball thrown */
    private List<List<TextView>> ballsTextViews = null;
    /** TextViews showing score of frame */
    private List<TextView> framesTextViews = null;
    /** CircleButtons which manipulate pins */
    private List<CircleButton> pinButtons = null;
    /** List of arrays representing state of pins */
    private List<List<boolean[]>> balls = null;
    /** Scores of current games */
    private int[] gameScores = null;

    /** HorizontalScrollView displaying score tables */
    private HorizontalScrollView hsvFrames = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        numberOfGames = preferences.getInt(LeagueEntry.TABLE_NAME + "." + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, -1);

        Intent intent = getIntent();
        gameID = intent.getLongArrayExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID);
        frameID = intent.getLongArrayExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params;
        hsvFrames = (HorizontalScrollView)findViewById(R.id.hsv_frames);
        ballsTextViews = new ArrayList<List<TextView>>();
        framesTextViews = new ArrayList<TextView>();
        balls = new ArrayList<List<boolean[]>>();
        gameScores = new int[numberOfGames];

        for (int i = 0; i < Constants.NUMBER_OF_FRAMES; i++)
        {
            ballsTextViews.add(new ArrayList<TextView>());
            balls.add(new ArrayList<boolean[]>());

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
            framesTextViews.add(frameText);

            for (int j = 0; j < 3; j++)
            {
                TextView ballText = new TextView(this);
                ballText.setBackgroundResource(R.drawable.text_frame_background);
                ballText.setGravity(Gravity.CENTER);
                params = new RelativeLayout.LayoutParams(getPixelsFromDP(40), getPixelsFromDP(40));
                params.leftMargin = getPixelsFromDP(120 * i + j * 40);
                params.topMargin = 0;
                relativeLayout.addView(ballText, params);
                ballsTextViews.get(i).add(ballText);
            }

            TextView textView = new TextView(this);
            textView.setText(String.valueOf(i + 1));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            params = new RelativeLayout.LayoutParams(getPixelsFromDP(12), getPixelsFromDP(4));
            params.leftMargin = getPixelsFromDP(120 * i + 60);
            params.topMargin = getPixelsFromDP(128);
            relativeLayout.addView(textView, params);
        }
        hsvFrames.addView(relativeLayout);

        pinButtons = new ArrayList<CircleButton>();
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
            pinButtons.add(circleButton);
            circleButton.setOnClickListener(this);
        }

        findViewById(R.id.button_next_frame).setOnClickListener(this);
        findViewById(R.id.button_prev_frame).setOnClickListener(this);
        findViewById(R.id.button_save_game).setOnClickListener(this);

        GradientDrawable drawable = (GradientDrawable)framesTextViews.get(currentFrame).getBackground();
        drawable.setColor(Color.RED);
        drawable = (GradientDrawable)ballsTextViews.get(currentFrame).get(currentBall).getBackground();
        drawable.setColor(Color.RED);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpWidth = (int)(displayMetrics.widthPixels / displayMetrics.density);
        relativeLayout = (RelativeLayout)findViewById(R.id.layout_games);
        for (int i = 0; i < numberOfGames; i++)
        {
            Button button = new Button(this);
            switch(i)
            {
                case 0:button.setId(R.id.button_game_0); break;
                case 1:button.setId(R.id.button_game_1); break;
                case 2:button.setId(R.id.button_game_2); break;
                case 3:button.setId(R.id.button_game_3); break;
                case 4:button.setId(R.id.button_game_4); break;
                default: //do nothing
            }
            button.setOnClickListener(this);
            button.setText("Game " + (i + 1));
            params = new RelativeLayout.LayoutParams(getPixelsFromDP(dpWidth / numberOfGames), getPixelsFromDP(48));
            params.topMargin = 0;
            params.leftMargin = getPixelsFromDP((dpWidth / numberOfGames) * i);
            relativeLayout.addView(button, params);
        }

        //Loads first game from memory
        loadGameFromDatabase(0);
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
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_game_stats:
                showGameStats();
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
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_GAME, gameID[currentGame])
                .apply();

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
        int frameToSet = 0;
        int ballToSet = 0;
        int gameToSet = 0;

        switch(view.getId())
        {
            case R.id.button_game_4: gameToSet++;
            case R.id.button_game_3: gameToSet++;
            case R.id.button_game_2: gameToSet++;
            case R.id.button_game_1: gameToSet++;
            case R.id.button_game_0:
                //Switching games
                saveGameToDatabase(false);
                loadGameFromDatabase(gameToSet);
                break;
            case R.id.button_save_game:
                //Hard coded game save
                //TODO delete save button in final
                saveGameToDatabase(true);
                break;
            case R.id.button_next_frame:
                //Clears the coloring of the current frame, increases the ball and/or
                //frame if possible, then recolors the new frame/ball
                clearFrameColor();
                if (areFramesEqual(balls.get(currentFrame).get(currentBall), Constants.FRAME_CLEAR))
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
                        while (!areFramesEqual(balls.get(currentFrame).get(currentBall), Constants.FRAME_CLEAR) && currentBall < 2)
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
                updateFrameColor();
                break;
            case R.id.button_pin_4: ballToSet++;
            case R.id.button_pin_3: ballToSet++;
            case R.id.button_pin_2: ballToSet++;
            case R.id.button_pin_1: ballToSet++;
            case R.id.button_pin_0:
                //When user selects a pin button
                boolean currentPinState = balls.get(currentFrame).get(currentBall)[ballToSet];
                if (!currentPinState)
                {
                    //pin was standing
                    pinButtons.get(ballToSet).setColor(Color.parseColor(COLOR_PIN_KNOCKED));
                    for (int i = currentBall; i < 3; i++)
                    {
                        balls.get(currentFrame).get(i)[ballToSet] = true;
                    }
                    if (areFramesEqual(balls.get(currentFrame).get(currentBall), Constants.FRAME_CLEAR))
                    {
                        clearFrameColor();
                        if (currentFrame == 9)
                        {
                            if (currentBall < 2)
                            {
                                currentBall++;
                                for (int i = 0; i < 5; i++)
                                {
                                    balls.get(currentFrame).get(currentBall)[i] = true;
                                }
                            }
                        }
                        else
                        {
                            //TODO: ifAutoContinueToNextFrame() ?
                            currentBall = 0;
                            currentFrame++;
                        }
                        updateFrameColor();
                    }
                }
                else
                {
                    //pin was down
                    pinButtons.get(ballToSet).setColor(Color.parseColor(COLOR_PIN_STANDING));
                    balls.get(currentFrame).get(currentBall)[ballToSet] = false;
                }
                updateScore();
                updateBalls(currentFrame);
                break;
            default:
                throw new RuntimeException("GameActivity#onClick unknown button ID");
        }
    }

    /**
     * Gets the score value of the frame from the balls
     *
     * @param frame the frame to get score of
     * @return score of the frame, in a 5 pin game
     */
    private int getValueOfFrame(boolean[] frame)
    {
        int frameValue = 0;
        for (int i = 0; i < frame.length; i++)
        {
            if (frame[i])
            {
                switch(i)
                {
                    case 0:case 4: frameValue += 2; break;
                    case 1:case 3: frameValue += 3; break;
                    case 2: frameValue += 5; break;
                    default: //do nothing
                }
            }
        }
        return frameValue;
    }

    /**
     * Gets textual value of ball based on surrounding balls
     *
     * @param ballsOfFrame list of all balls in the frame
     * @param ball the ball to get the value of
     * @return textual value of the ball
     */
    private String getValueOfBall(List<boolean[]> ballsOfFrame, int ball)
    {
        boolean[] pinAlreadyKnockedDown = new boolean[5];

        for (int i = 0; i < ball; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                if (ballsOfFrame.get(i)[j])
                {
                    pinAlreadyKnockedDown[j] = true;
                }
            }
        }

        int ballValue = 0;
        for (int i = 0; i < 5; i++)
        {
            if (ballsOfFrame.get(ball)[i] && !pinAlreadyKnockedDown[i])
            {
                switch(i)
                {
                    case 0:case 4: ballValue += 2; break;
                    case 1:case 3: ballValue += 3; break;
                    case 2: ballValue += 5; break;
                    default: //do nothing
                }
            }
        }

        switch(ballValue)
        {
            default: throw new RuntimeException("Invalid value for ball: " + ballValue);
            case 0: return Constants.BALL_EMPTY;
            case 2:case 3:case 4:case 6:case 9:case 12: return String.valueOf(ballValue);
            case 5:
                if (ball == 0 && ballsOfFrame.get(ball)[2] && !pinAlreadyKnockedDown[2])
                {
                    return Constants.BALL_HEAD_PIN;
                }
                else
                {
                    return "5";
                }
            case 7:
                if (ball == 0 && ballsOfFrame.get(ball)[2])
                {
                    return Constants.BALL_HEAD_PIN_2;
                }
                else
                {
                    return "7";
                }
            case 8:
                if (ball == 0 && ballsOfFrame.get(ball)[2])
                {
                    return Constants.BALL_SPLIT;
                }
                else
                    return "8";
            case 10:
                if (ball == 0 && ballsOfFrame.get(ball)[2]
                        && ((ballsOfFrame.get(ball)[0] && ballsOfFrame.get(ball)[1])
                        || (ballsOfFrame.get(ball)[3] && ballsOfFrame.get(ball)[4])))
                {
                    return Constants.BALL_CHOP_OFF;
                }
                else
                {
                    return "10";
                }
            case 11:
                if (ball == 0 && ballsOfFrame.get(ball)[2])
                {
                    return Constants.BALL_ACE;
                }
                else
                    return "11";
            case 13:
                if (ball == 0 && ballsOfFrame.get(ball)[0])
                {
                    return Constants.BALL_LEFT;
                }
                else if (ball == 0 && ballsOfFrame.get(ball)[4])
                {
                    return Constants.BALL_RIGHT;
                }
                else
                {
                    return "13";
                }
            case 15:
                if (ball == 0)
                {
                    return Constants.BALL_STRIKE;
                }
                else if (ball == 1)
                {
                    return Constants.BALL_SPARE;
                }
                else
                {
                    return "15";
                }
        }
    }

    /**
     * Sets the TextView displaying score corresponding to the current frame
     * to the textual value of the ball from getValueOfBall
     */
    private void updateBalls(int currentFrame)
    {
        /*for (int f = currentFrame; f < Constants.NUMBER_OF_FRAMES; f++)
        {*/
            if (areFramesEqual(balls.get(currentFrame).get(0), Constants.FRAME_CLEAR))
            {
                ballsTextViews.get(currentFrame).get(0).setText(Constants.BALL_STRIKE);
                ballsTextViews.get(currentFrame).get(1).setText(Constants.BALL_EMPTY);
                ballsTextViews.get(currentFrame).get(2).setText(Constants.BALL_EMPTY);
            }
            else if (areFramesEqual(balls.get(currentFrame).get(1), Constants.FRAME_CLEAR))
            {
                ballsTextViews.get(currentFrame).get(0).setText(getValueOfBall(balls.get(currentFrame), 0));
                ballsTextViews.get(currentFrame).get(1).setText(Constants.BALL_SPARE);
                ballsTextViews.get(currentFrame).get(2).setText(Constants.BALL_EMPTY);
            }
            else
            {
                ballsTextViews.get(currentFrame).get(0).setText(getValueOfBall(balls.get(currentFrame), 0));
                ballsTextViews.get(currentFrame).get(1).setText(getValueOfBall(balls.get(currentFrame), 1));
                ballsTextViews.get(currentFrame).get(2).setText(getValueOfBall(balls.get(currentFrame), 2));
            }
        //}
    }

    /**
     * Updates the score values of all frames
     */
    private void updateScore()
    {
        int[] frameScores = new int[10];

        for (int f = Constants.NUMBER_OF_FRAMES - 1; f >= 0; f--)
        {
            if (f == Constants.NUMBER_OF_FRAMES - 1)
            {
                for (int b = 2; b >= 0; b--)
                {
                    switch(b)
                    {
                        case 2:
                            frameScores[f] += getValueOfFrame(balls.get(f).get(b));
                            break;
                        case 1:
                        case 0:
                            if (areFramesEqual(balls.get(f).get(b), Constants.FRAME_CLEAR))
                            {
                                frameScores[f] += getValueOfFrame(balls.get(f).get(b));
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
                    if (areFramesEqual(balls.get(f).get(b), Constants.FRAME_CLEAR))
                    {
                        frameScores[f] += getValueOfFrame(balls.get(f).get(b));
                        frameScores[f] += getValueOfFrame(balls.get(f + 1).get(0));
                        if (b == 0)
                        {
                            if (f == 8 || !areFramesEqual(balls.get(f + 1).get(0), Constants.FRAME_CLEAR))
                            {
                                frameScores[f] += getValueOfFrame(balls.get(f + 1).get(1));
                            }
                            else if (areFramesEqual(balls.get(f + 1).get(0), Constants.FRAME_CLEAR))
                            {
                                frameScores[f] += getValueOfFrame(balls.get(f + 1).get(0));
                            }
                        }
                        break;
                    }
                    else if (b == 2)
                    {
                        frameScores[f] += getValueOfFrame(balls.get(f).get(b));
                    }
                }
            }
        }

        int totalScore = 0;
        for (int i = 0; i < frameScores.length; i++)
        {
            totalScore += frameScores[i];
            framesTextViews.get(i).setText(String.valueOf(totalScore));
        }
        gameScores[currentGame] = totalScore;
    }

    /**
     * Sets the background color of the current frame to white
     */
    private void clearFrameColor()
    {
        GradientDrawable drawable = (GradientDrawable) ballsTextViews.get(currentFrame).get(currentBall).getBackground();
        drawable.setColor(Color.WHITE);
        drawable = (GradientDrawable) framesTextViews.get(currentFrame).getBackground();
        drawable.setColor(Color.WHITE);
    }

    /**
     * Sets the background color of the current frame to red
     */
    private void updateFrameColor()
    {
        GradientDrawable drawable = (GradientDrawable) ballsTextViews.get(currentFrame).get(currentBall).getBackground();
        drawable.setColor(Color.RED);
        drawable = (GradientDrawable) framesTextViews.get(currentFrame).getBackground();
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
            if (balls.get(currentFrame).get(currentBall)[i])
            {
                button.setColor(Color.parseColor(COLOR_PIN_KNOCKED));
            }
            else
            {
                button.setColor(Color.parseColor(COLOR_PIN_STANDING));
            }
        }
        focusOnFrame();
    }

    /**
     * Checks if two frames are equivalent
     *
     * @param frame boolean array representing pins
     * @param frameToCompare boolean array representing a certain frame
     * @return
     */
    private boolean areFramesEqual(boolean[] frame, boolean[] frameToCompare)
    {
        for (int i = 0; i < frame.length; i++)
        {
            try
            {
                if (frame[i] != frameToCompare[i])
                {
                    return false;
                }
            }
            catch (IndexOutOfBoundsException ex)
            {
                Log.w("GameActivity", "areFramesEqual index out of bounds. " + frame.length + " != " + frameToCompare.length);
            }
        }

        return true;
    }

    /**
     * Saves the game to the database
     *
     * @param shouldShowSavedMessage if true, displays a toast message if successful
     */
    private void saveGameToDatabase(boolean shouldShowSavedMessage)
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();
        ContentValues values;

        Log.w("GameActivity", "Game score: " + gameScores[currentGame]);

        database.beginTransaction();
        try
        {
            values = new ContentValues();
            values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, gameScores[currentGame]);
            database.update(GameEntry.TABLE_NAME,
                    values,
                    GameEntry._ID + "=?",
                    new String[]{String.valueOf(gameID[currentGame])});

            for (int i = 0; i < 10; i++)
            {
                values = new ContentValues();
                values.put(FrameEntry.COLUMN_NAME_BALL[0], String.valueOf(balls.get(i).get(0)));
                values.put(FrameEntry.COLUMN_NAME_BALL[1], String.valueOf(balls.get(i).get(1)));
                values.put(FrameEntry.COLUMN_NAME_BALL[2], String.valueOf(balls.get(i).get(2)));
                /*Log.w("SavingGame", String.valueOf(balls.get(i).get(0)));
                Log.w("SavingGame", "FrameID: " + frameID[currentGame * 10 + i]);*/
                database.update(FrameEntry.TABLE_NAME,
                        values,
                        FrameEntry._ID + "=?",
                        new String[]{String.valueOf(frameID[currentGame * 10 + i])});
            }

            database.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w("GameActivity", "Error saving game " + currentGame);
        }
        finally
        {
            database.endTransaction();
        }

        if (shouldShowSavedMessage)
        {
            Toast.makeText(this, "Game saved!", Toast.LENGTH_SHORT).show();
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
                new String[]{FrameEntry.COLUMN_NAME_BALL[0], FrameEntry.COLUMN_NAME_BALL[1], FrameEntry.COLUMN_NAME_BALL[2], FrameEntry._ID},
                FrameEntry.COLUMN_NAME_GAME_ID + "=?",
                new String[]{String.valueOf(gameID[currentGame])},
                null,
                null,
                FrameEntry.COLUMN_NAME_FRAME_NUMBER);

        int currentFrameIterator = 0;
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                for (int i = 0; i < 3; i++)
                {
                    String frameString = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[i]));
                    boolean[] frameChar = {getBoolean(frameString.charAt(0)), getBoolean(frameString.charAt(1)), getBoolean(frameString.charAt(2)), getBoolean(frameString.charAt(3)), getBoolean(frameString.charAt(4))};
                    balls.get(currentFrameIterator).add(frameChar);
                    if (balls.get(currentFrameIterator).size() > 3)
                    {
                        balls.get(currentFrameIterator).remove(0);
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
        updateFrameColor();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        saveGameToDatabase(false);
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
                    hsvFrames.smoothScrollTo(framesTextViews.get(currentFrame).getLeft(), 0);
                }
            });
    }

    /**
     * Gets a boolean from a char
     * @param input char to convert to boolean
     * @return true if input is equal to '1', false otherwise
     */
    private boolean getBoolean(char input)
    {
        return input == '1';
    }
}

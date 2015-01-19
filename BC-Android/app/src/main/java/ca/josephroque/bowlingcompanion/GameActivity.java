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
import java.util.prefs.PreferenceChangeEvent;

import at.markushi.ui.CircleButton;
import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

public class GameActivity extends ActionBarActivity implements View.OnClickListener
{

    private static final String COLOR_PIN_KNOCKED = "#000000";
    private static final String COLOR_PIN_STANDING = "#99CC00";

    private long[] gameID = null;
    private long[] frameID = null;
    private int numberOfGames = -1;

    private int currentGame = 0;
    private int currentFrame = 0;
    private int currentBall = 0;

    private List<List<TextView>> ballsTextViews = null;
    private List<TextView> framesTextViews = null;
    private List<CircleButton> pinButtons = null;
    private List<List<char[]>> balls = null;
    private int[] gameScores = null;

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
        balls = new ArrayList<List<char[]>>();
        gameScores = new int[numberOfGames];

        /*params = (RelativeLayout.LayoutParams)hsvFrames.getLayoutParams();
        params.height = params.height + getPixelsFromDP(12);
        hsvFrames.setLayoutParams(params);*/

        for (int i = 0; i < 10; i++)
        {
            ballsTextViews.add(new ArrayList<TextView>());
            balls.add(new ArrayList<char[]>());

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

    private void showGameStats()
    {
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_GAME, gameID[currentGame])
                .apply();

        Intent statsIntent = new Intent(GameActivity.this, StatsActivity.class);
        startActivity(statsIntent);
    }

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
                saveGameToDatabase(false);
                loadGameFromDatabase(gameToSet);
                break;
            case R.id.button_save_game:
                saveGameToDatabase(true);
                break;
            case R.id.button_next_frame:
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
                char currentPinState = balls.get(currentFrame).get(currentBall)[ballToSet];
                if (currentPinState == 'o')
                {
                    //pin was standing
                    pinButtons.get(ballToSet).setColor(Color.parseColor(COLOR_PIN_KNOCKED));
                    for (int i = currentBall; i < 3; i++)
                    {
                        balls.get(currentFrame).get(i)[ballToSet] = 'x';
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
                                    balls.get(currentFrame).get(currentBall)[i] = 'o';
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
                    balls.get(currentFrame).get(currentBall)[ballToSet] = 'o';
                }
                updateScore();
                updateBalls();
                break;
            default:
                throw new RuntimeException("GameActivity#onClick unknown button ID");
        }
    }

    private int getValueOfFrame(char[] frame)
    {
        int frameValue = 0;
        for (int i = 0; i < frame.length; i++)
        {
            if (frame[i] == 'x')
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

    private String getValueOfBall(List<char[]> ballsOfFrame, int ball)
    {
        boolean[] pinAlreadyKnockedDown = new boolean[5];

        for (int i = 0; i < ball; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                if (ballsOfFrame.get(i)[j] == 'x')
                {
                    pinAlreadyKnockedDown[j] = true;
                }
            }
        }

        int ballValue = 0;
        for (int i = 0; i < 5; i++)
        {
            if (ballsOfFrame.get(ball)[i] == 'x' && !pinAlreadyKnockedDown[i])
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
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x' && !pinAlreadyKnockedDown[2])
                {
                    return Constants.BALL_HEAD_PIN;
                }
                else
                {
                    return "5";
                }
            case 7:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x')
                {
                    return Constants.BALL_HEAD_PIN_2;
                }
                else
                {
                    return "7";
                }
            case 8:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x')
                {
                    return Constants.BALL_SPLIT;
                }
                else
                    return "8";
            case 10:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x'
                        && ((ballsOfFrame.get(ball)[0] == 'x' && ballsOfFrame.get(ball)[1] == 'x')
                        || (ballsOfFrame.get(ball)[3] == 'x' && ballsOfFrame.get(ball)[4] == 'x')))
                {
                    return Constants.BALL_CHOP_OFF;
                }
                else
                {
                    return "10";
                }
            case 11:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x')
                {
                    return Constants.BALL_ACE;
                }
                else
                    return "11";
            case 13:
                if (ball == 0 && ballsOfFrame.get(ball)[0] == 'o')
                {
                    return Constants.BALL_LEFT;
                }
                else if (ball == 0 && ballsOfFrame.get(ball)[4] == 'o')
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

    private void updateBalls()
    {
        for (int f = 0; f < 10; f++)
        {
            if (areFramesEqual(balls.get(f).get(0), Constants.FRAME_CLEAR))
            {
                ballsTextViews.get(f).get(0).setText(Constants.BALL_STRIKE);
                ballsTextViews.get(f).get(1).setText(Constants.BALL_EMPTY);
                ballsTextViews.get(f).get(2).setText(Constants.BALL_EMPTY);
            }
            else if (areFramesEqual(balls.get(f).get(1), Constants.FRAME_CLEAR))
            {
                ballsTextViews.get(f).get(0).setText(getValueOfBall(balls.get(f), 0));
                ballsTextViews.get(f).get(1).setText(Constants.BALL_SPARE);
                ballsTextViews.get(f).get(2).setText(Constants.BALL_EMPTY);
            }
            else
            {
                ballsTextViews.get(f).get(0).setText(getValueOfBall(balls.get(f), 0));
                ballsTextViews.get(f).get(1).setText(getValueOfBall(balls.get(f), 1));
                ballsTextViews.get(f).get(2).setText(getValueOfBall(balls.get(f), 2));
            }
        }
    }

    private void updateScore()
    {
        int[] frameScores = new int[10];

        for (int f = 9; f >= 0; f--)
        {
            if (f == 9)
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

    private void clearFrameColor()
    {
        GradientDrawable drawable = (GradientDrawable) ballsTextViews.get(currentFrame).get(currentBall).getBackground();
        drawable.setColor(Color.WHITE);
        drawable = (GradientDrawable) framesTextViews.get(currentFrame).getBackground();
        drawable.setColor(Color.WHITE);
    }

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
            if (balls.get(currentFrame).get(currentBall)[i] == 'x')
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

    private boolean areFramesEqual(char[] frame, String frameToCompare)
    {
        for (int i = 0; i < frame.length; i++)
        {
            try
            {
                if (frame[i] != frameToCompare.charAt(i))
                {
                    return false;
                }
            }
            catch (IndexOutOfBoundsException ex)
            {
                Log.w("GameActivity", "areFramesEqual index out of bounds. " + frame.length + " != " + frameToCompare.length());
            }
        }

        return true;
    }

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
                    /*Log.w("LoadingGame", frameString);
                    Log.w("LoadingGame", "FrameID: " + cursor.getString(cursor.getColumnIndex(FrameEntry._ID)));*/
                    char[] frameChar = {frameString.charAt(0), frameString.charAt(1), frameString.charAt(2), frameString.charAt(3), frameString.charAt(4)};
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
        updateBalls();
        updateFrameColor();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        saveGameToDatabase(false);
    }

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
}

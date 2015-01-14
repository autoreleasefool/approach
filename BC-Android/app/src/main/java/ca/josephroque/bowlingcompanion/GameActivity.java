package ca.josephroque.bowlingcompanion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;

public class GameActivity extends ActionBarActivity implements View.OnClickListener
{

    private static final String FRAME_CLEAR = "xxxxx";
    private static final String BALL_STRIKE = "X";
    private static final String BALL_SPARE = "/";
    private static final String BALL_LEFT = "L";
    private static final String BALL_RIGHT = "R";
    private static final String BALL_ACE = "A";
    private static final String BALL_CHOP_OFF = "C/O";
    private static final String BALL_SPLIT = "Sp";
    private static final String BALL_HEAD_PIN = "Hp";
    private static final String BALL_HEAD_PIN_2 = "H2";
    private static final String BALL_HEAD_PIN_3 = "H3";
    private static final String BALL_EMPTY = "-";

    private long bowlerID = -1;
    private long leagueID = -1;
    private long seriesID = -1;
    private long[] gameID = null;
    private long[] frameID = null;

    private int currentGame = 0;
    private int currentFrame = 0;
    private int currentBall = 0;

    private List<List<TextView>> ballsTextViews = null;
    private List<TextView> framesTextViews = null;
    private List<List<char[]>> balls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        bowlerID = intent.getLongExtra(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, -1);
        leagueID = intent.getLongExtra(LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID, -1);
        seriesID = intent.getLongExtra(SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID, -1);
        gameID = intent.getLongArrayExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID);
        frameID = intent.getLongArrayExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params;
        HorizontalScrollView hsvFrames = (HorizontalScrollView)findViewById(R.id.hsv_frames);
        ballsTextViews = new ArrayList<List<TextView>>(10);
        framesTextViews = new ArrayList<TextView>(10);
        balls = new ArrayList<List<char[]>>(10);

        for (int i = 0; i < 10; i++)
        {
            ballsTextViews.add(new ArrayList<TextView>(3));
            balls.add(new ArrayList<char[]>(3));

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
                balls.get(i).add(new char[]{'o', 'o', 'o', 'o', 'o'});
                TextView ballText = new TextView(this);
                ballText.setBackgroundResource(R.drawable.text_frame_background);
                ballText.setGravity(Gravity.CENTER);
                params = new RelativeLayout.LayoutParams(getPixelsFromDP(40), getPixelsFromDP(40));
                params.leftMargin = getPixelsFromDP(120 * i + j * 40);
                params.topMargin = 0;
                relativeLayout.addView(ballText, params);
                ballsTextViews.get(i).add(ballText);
            }
        }
        hsvFrames.addView(relativeLayout);

        findViewById(R.id.button_pin_0).setOnClickListener(this);
        findViewById(R.id.button_pin_1).setOnClickListener(this);
        findViewById(R.id.button_pin_2).setOnClickListener(this);
        findViewById(R.id.button_pin_3).setOnClickListener(this);
        findViewById(R.id.button_pin_4).setOnClickListener(this);
        findViewById(R.id.button_next_frame).setOnClickListener(this);
        findViewById(R.id.button_prev_frame).setOnClickListener(this);
        findViewById(R.id.button_save_game).setOnClickListener(this);

        GradientDrawable drawable = (GradientDrawable)framesTextViews.get(currentFrame).getBackground();
        drawable.setColor(Color.RED);
        drawable = (GradientDrawable)ballsTextViews.get(currentFrame).get(currentBall).getBackground();
        drawable.setColor(Color.RED);
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
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showGameStats()
    {
        //TODO: showGameStats()
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

        switch(view.getId())
        {
            case R.id.button_save_game:
                saveGameToDatabase();
                break;
            case R.id.button_next_frame:
                clearFrameColor();
                if (areFramesEqual(balls.get(currentFrame).get(currentBall), FRAME_CLEAR))
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
                        while (!areFramesEqual(balls.get(currentFrame).get(currentBall), FRAME_CLEAR) && currentBall <= 2)
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
                GradientDrawable drawable = (GradientDrawable) framesTextViews.get(currentFrame).getBackground();
                drawable.setColor(Color.WHITE);
                drawable = (GradientDrawable) ballsTextViews.get(currentFrame).get(currentBall).getBackground();
                drawable.setColor(Color.WHITE);

                currentFrame = frameToSet;
                currentBall = 0;

                drawable = (GradientDrawable) framesTextViews.get(currentFrame).getBackground();
                drawable.setColor(Color.RED);
                drawable = (GradientDrawable) ballsTextViews.get(currentFrame).get(currentBall).getBackground();
                drawable.setColor(Color.RED);
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
                    balls.get(currentFrame).get(currentBall)[ballToSet] = 'x';
                    if (areFramesEqual(balls.get(currentFrame).get(currentBall), FRAME_CLEAR))
                    {
                        if (currentFrame == 9)
                        {
                            if (currentBall < 2)
                            {
                                currentBall++;
                            }
                        }
                        else
                        {
                            //TODO: ifAutoContinueToNextFrame() ?
                            currentBall = 0;
                            currentFrame++;
                        }
                    }
                }
                else
                {
                    //pin was down
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
            case 0: return BALL_EMPTY;
            case 2:case 3:case 4:case 6:case 9:case 12: return String.valueOf(ballValue);
            case 5:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x' && !pinAlreadyKnockedDown[2])
                {
                    return BALL_HEAD_PIN;
                }
                else
                {
                    return "5";
                }
            case 7:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x')
                {
                    return BALL_HEAD_PIN_2;
                }
                else
                {
                    return "7";
                }
            case 8:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x')
                {
                    return BALL_HEAD_PIN_3;
                }
                else
                    return "8";
            case 10:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x'
                        && ((ballsOfFrame.get(ball)[0] == 'x' && ballsOfFrame.get(ball)[1] == 'x')
                        || (ballsOfFrame.get(ball)[3] == 'x' && ballsOfFrame.get(ball)[4] == 'x')))
                {
                    return BALL_CHOP_OFF;
                }
                else
                {
                    return "10";
                }
            case 11:
                if (ball == 0 && ballsOfFrame.get(ball)[2] == 'x')
                {
                    return BALL_ACE;
                }
                else
                    return "11";
            case 13:
                if (ball == 0 && ballsOfFrame.get(ball)[0] == 'o')
                {
                    return BALL_LEFT;
                }
                else if (ball == 0 && ballsOfFrame.get(ball)[4] == 'o')
                {
                    return BALL_RIGHT;
                }
                else
                {
                    return "13";
                }
            case 15:
                if (ball == 0)
                {
                    return BALL_STRIKE;
                }
                else if (ball == 1)
                {
                    return BALL_SPARE;
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
            if (areFramesEqual(balls.get(f).get(0), FRAME_CLEAR))
            {
                ballsTextViews.get(f).get(0).setText(BALL_STRIKE);
                ballsTextViews.get(f).get(1).setText(BALL_EMPTY);
                ballsTextViews.get(f).get(2).setText(BALL_EMPTY);
            }
            else if (areFramesEqual(balls.get(f).get(1), FRAME_CLEAR))
            {
                ballsTextViews.get(f).get(0).setText(getValueOfBall(balls.get(f), 0));
                ballsTextViews.get(f).get(1).setText(BALL_SPARE);
                ballsTextViews.get(f).get(2).setText(BALL_EMPTY);
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
                            if (areFramesEqual(balls.get(f).get(b), FRAME_CLEAR))
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
                    if (areFramesEqual(balls.get(f).get(b), FRAME_CLEAR))
                    {
                        frameScores[f] += getValueOfFrame(balls.get(f).get(b));
                        frameScores[f] += getValueOfFrame(balls.get(f + 1).get(0));
                        if (b == 0)
                        {
                            if (f == 8 || !areFramesEqual(balls.get(f + 1).get(0),FRAME_CLEAR))
                            {
                                frameScores[f] += getValueOfFrame(balls.get(f + 1).get(1));
                            }
                            else if (areFramesEqual(balls.get(f + 1).get(0), FRAME_CLEAR))
                            {
                                frameScores[f] += getValueOfFrame(balls.get(f + 1).get(0));
                            }
                        }
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
            framesTextViews.get(i).setText(totalScore);
        }
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

    private void saveGameToDatabase()
    {

    }
}

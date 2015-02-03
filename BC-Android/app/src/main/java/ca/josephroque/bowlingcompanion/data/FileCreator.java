package ca.josephroque.bowlingcompanion.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Arrays;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-02-02.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.image
 * in project Bowling Companion
 */
public class FileCreator
{

    private static final int BITMAP_GAME_WIDTH = 660;
    private static final int BITMAP_GAME_HEIGHT = 45;
    private static final int BITMAP_GAME_BALL_HEIGHT = 15;
    private static final int BITMAP_GAME_BALL_WIDTH = 20;
    private static final int BITMAP_GAME_FRAME_HEIGHT = 30;
    private static final int BITMAP_GAME_FRAME_WIDTH = 60;

    public static Bitmap createImageFromGame(Context context, boolean[][][] ballsOfFrame, boolean[][] fouls)
    {
        Bitmap bitmap = Bitmap.createBitmap(BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paintBlackOutline = new Paint();
        paintBlackOutline.setColor(Color.BLACK);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);

        int frameScores[] = new int[Constants.NUMBER_OF_FRAMES];

        int foulCount = 0;
        for (int frame = Constants.LAST_FRAME; frame >= 0; frame--)
        {
            if (frame == Constants.LAST_FRAME)
            {
                if (Arrays.equals(ballsOfFrame[frame][0], Constants.FRAME_CLEAR))
                {
                    canvas.drawText(Constants.BALL_STRIKE, BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    if (Arrays.equals(ballsOfFrame[frame][1], Constants.FRAME_CLEAR))
                    {
                        canvas.drawText(Constants.BALL_STRIKE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                        canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][2], 2, true), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][1], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                        if (Arrays.equals(ballsOfFrame[frame][2], Constants.FRAME_CLEAR))
                            canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                        else
                            canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    }
                }
                else
                {
                    canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][0], 0, false), BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    if (Arrays.equals(ballsOfFrame[frame][1], Constants.FRAME_CLEAR))
                    {
                        canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                        canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][2], 2, true), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    }
                }
            }
            else
            {
                canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 0, false), BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                if (!Arrays.equals(ballsOfFrame[frame][0], Constants.FRAME_CLEAR))
                {
                    if (Arrays.equals(ballsOfFrame[frame][1], Constants.FRAME_CLEAR))
                    {
                        canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                        canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    }
                }
                else
                {
                    canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                    canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, textPaint);
                }
            }

            for (int ball = 0; ball < ballsOfFrame[frame].length; ball++)
            {
                if (fouls[frame][ball])
                {
                    foulCount++;
                    canvas.drawText("F", BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame + BITMAP_GAME_BALL_WIDTH / 2, BITMAP_GAME_BALL_HEIGHT + 15, textPaint);
                }
                canvas.drawLine(BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame, 0, BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, paintBlackOutline);
            }
            canvas.drawLine(BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_FRAME_HEIGHT + BITMAP_GAME_BALL_HEIGHT, paintBlackOutline);

            if (frame == Constants.LAST_FRAME)
            {
                for (int b = 2; b >= 0; b--)
                {
                    switch(b)
                    {
                        case 2:
                            frameScores[frame] += GameScore.getValueOfFrame(ballsOfFrame[frame][b]);
                            break;
                        case 1:
                        case 0:
                            if (Arrays.equals(ballsOfFrame[frame][b], Constants.FRAME_CLEAR))
                            {
                                frameScores[frame] += GameScore.getValueOfFrame(ballsOfFrame[frame][b]);
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
                    if (b < 2 && Arrays.equals(ballsOfFrame[frame][b], Constants.FRAME_CLEAR))
                    {
                        frameScores[frame] += GameScore.getValueOfFrame(ballsOfFrame[frame][b]);
                        frameScores[frame] += GameScore.getValueOfFrame(ballsOfFrame[frame + 1][0]);
                        if (b == 0)
                        {
                            if (frame == Constants.LAST_FRAME - 1)
                            {
                                if (frameScores[frame] == 30)
                                {
                                    frameScores[frame] += GameScore.getValueOfFrame(ballsOfFrame[frame + 1][1]);
                                }
                                else
                                {
                                    frameScores[frame] += GameScore.getValueOfFrameDifference(ballsOfFrame[frame + 1][0], ballsOfFrame[frame + 1][1]);
                                }
                            }
                            else if (frameScores[frame] < 30)
                            {
                                frameScores[frame] += GameScore.getValueOfFrameDifference(ballsOfFrame[frame + 1][0], ballsOfFrame[frame + 1][1]);
                            }
                            else
                            {
                                frameScores[frame] += GameScore.getValueOfFrame(ballsOfFrame[frame + 2][0]);
                            }
                        }
                        break;
                    }
                    else if (b == 2)
                    {
                        frameScores[frame] += GameScore.getValueOfFrame(ballsOfFrame[frame][b]);
                    }
                }
            }
        }

        int totalScore = 0;
        for (int i = 0; i < frameScores.length; i++)
        {
            totalScore += frameScores[i];
            canvas.drawText(String.valueOf(totalScore), i * BITMAP_GAME_FRAME_WIDTH + BITMAP_GAME_FRAME_WIDTH / 2, BITMAP_GAME_BALL_HEIGHT + BITMAP_GAME_FRAME_HEIGHT - 10, textPaint);
        }

        int scoreWithFouls = totalScore - 15 * foulCount;
        if (scoreWithFouls < 0)
            scoreWithFouls = 0;
        canvas.drawText(String.valueOf(scoreWithFouls), BITMAP_GAME_WIDTH - BITMAP_GAME_FRAME_WIDTH / 2, BITMAP_GAME_HEIGHT / 2, textPaint);


        canvas.drawLines(new float[]
                        {0,0,BITMAP_GAME_WIDTH, 0,
                        0,0,0,BITMAP_GAME_HEIGHT,
                        0,BITMAP_GAME_HEIGHT - 1,BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT - 1,
                        BITMAP_GAME_WIDTH - 1, 0, BITMAP_GAME_WIDTH - 1, BITMAP_GAME_HEIGHT,
                        0, BITMAP_GAME_BALL_HEIGHT, BITMAP_GAME_WIDTH - BITMAP_GAME_FRAME_WIDTH, BITMAP_GAME_BALL_HEIGHT,
                        BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, 0, BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, BITMAP_GAME_HEIGHT},
                paintBlackOutline);

        return bitmap;
    }
}

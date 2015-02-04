package ca.josephroque.bowlingcompanion.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static final String TAG = "FileCreator";

    private static final int BITMAP_GAME_WIDTH = 660;
    private static final int BITMAP_GAME_HEIGHT = 60;
    private static final int BITMAP_GAME_BALL_HEIGHT = 20;
    private static final int BITMAP_GAME_BALL_WIDTH = 20;
    private static final int BITMAP_GAME_FRAME_HEIGHT = 40;
    private static final int BITMAP_GAME_FRAME_WIDTH = 60;
    private static final int BITMAP_SERIES_GAME_NAME_WIDTH = 80;

    private static final float GAME_DEFAULT_FONT_SIZE = 12;
    private static final float GAME_SMALL_FONT_SIZE = 8;
    private static final float GAME_LARGE_FONT_SIZE = 16;

    private static final float BALL_TEXT_Y = BITMAP_GAME_BALL_HEIGHT / 2 + GAME_DEFAULT_FONT_SIZE / 2;

    public static Bitmap createImageFromGame(boolean[][][] ballsOfFrame, boolean[][] fouls)
    {
        Bitmap bitmap = Bitmap.createBitmap(BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paintBlackOutline = new Paint();
        paintBlackOutline.setColor(Color.BLACK);

        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(GAME_DEFAULT_FONT_SIZE);

        int frameScores[] = new int[Constants.NUMBER_OF_FRAMES];
        int foulCount = 0;

        for (int frame = Constants.LAST_FRAME; frame >= 0; frame--)
        {
            if (frame == Constants.LAST_FRAME)
            {
                if (Arrays.equals(ballsOfFrame[frame][0], Constants.FRAME_CLEAR))
                {
                    canvas.drawText(Constants.BALL_STRIKE, BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    if (Arrays.equals(ballsOfFrame[frame][1], Constants.FRAME_CLEAR))
                    {
                        canvas.drawText(Constants.BALL_STRIKE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][2], 2, true), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][1], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        if (Arrays.equals(ballsOfFrame[frame][2], Constants.FRAME_CLEAR))
                            canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        else
                            canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                }
                else
                {
                    canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][0], 0, false), BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    if (Arrays.equals(ballsOfFrame[frame][1], Constants.FRAME_CLEAR))
                    {
                        canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBall(ballsOfFrame[frame][2], 2, true), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                }
            }
            else
            {
                canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 0, false), BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                if (!Arrays.equals(ballsOfFrame[frame][0], Constants.FRAME_CLEAR))
                {
                    if (Arrays.equals(ballsOfFrame[frame][1], Constants.FRAME_CLEAR))
                    {
                        canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBallDifference(ballsOfFrame[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                }
                else
                {
                    canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                }
            }

            paintText.setTextSize(GAME_SMALL_FONT_SIZE);
            for (int ball = 0; ball < ballsOfFrame[frame].length; ball++)
            {
                if (fouls[frame][ball])
                {
                    foulCount++;
                    canvas.drawText("F", BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame + BITMAP_GAME_BALL_WIDTH / 2, BITMAP_GAME_BALL_HEIGHT + GAME_SMALL_FONT_SIZE + 2, paintText);
                }
                canvas.drawLine(BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame, 0, BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, paintBlackOutline);
            }
            canvas.drawLine(BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT, BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_FRAME_HEIGHT + BITMAP_GAME_BALL_HEIGHT, paintBlackOutline);
            paintText.setTextSize(GAME_DEFAULT_FONT_SIZE);

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
        paintText.setTextSize(GAME_LARGE_FONT_SIZE);
        for (int i = 0; i < frameScores.length; i++)
        {
            totalScore += frameScores[i];
            canvas.drawText(String.valueOf(totalScore), i * BITMAP_GAME_FRAME_WIDTH + BITMAP_GAME_FRAME_WIDTH / 2, BITMAP_GAME_HEIGHT - 8, paintText);
        }

        int scoreWithFouls = totalScore - 15 * foulCount;
        if (scoreWithFouls < 0)
            scoreWithFouls = 0;
        canvas.drawText(String.valueOf(scoreWithFouls), BITMAP_GAME_WIDTH - BITMAP_GAME_FRAME_WIDTH / 2, BITMAP_GAME_HEIGHT / 2 + GAME_LARGE_FONT_SIZE / 2, paintText);

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

    public static Bitmap createImageFromSeries(Context context, long[] gameIDs)
    {
        List<boolean[][][]> ballsOfGames = new ArrayList<boolean[][][]>();
        List<boolean[][]> foulsOfGames = new ArrayList<boolean[][]>();

        SQLiteDatabase database = DatabaseHelper.getInstance(context).getReadableDatabase();
        String[] rawImageArgs = new String[gameIDs.length];
        StringBuilder rawImageQueryBuilder = new StringBuilder("SELECT "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2] + ", "
                + FrameEntry.COLUMN_NAME_FOULS
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " LEFT JOIN " + FrameEntry.TABLE_NAME
                + " ON game." + GameEntry._ID + "=" + FrameEntry.COLUMN_NAME_GAME_ID
                + " WHERE game." + GameEntry._ID + "=?");
        rawImageArgs[0] = String.valueOf(gameIDs[0]);
        for (int i = 1; i < gameIDs.length; i++)
        {
            rawImageQueryBuilder.append(" OR game." + GameEntry._ID + "=?");
            rawImageArgs[i] = String.valueOf(gameIDs[i]);
        }
        rawImageQueryBuilder.append(" ORDER BY game." + GameEntry._ID + ", " + FrameEntry.COLUMN_NAME_FRAME_NUMBER);

        Cursor cursor = database.rawQuery(rawImageQueryBuilder.toString(), rawImageArgs);

        int currentFrame = 0;
        int currentGame = -1;
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                int frameNumber = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_NUMBER));
                if (frameNumber == 1)
                {
                    currentFrame = 0;
                    currentGame++;
                    ballsOfGames.add(new boolean[Constants.NUMBER_OF_FRAMES][3][5]);
                    foulsOfGames.add(new boolean[Constants.NUMBER_OF_FRAMES][3]);
                }

                for (int i = 0; i < 3; i++)
                {
                    String ballString = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[i]));
                    boolean[] ballBoolean = {GameScore.getBoolean(ballString.charAt(0)), GameScore.getBoolean(ballString.charAt(1)), GameScore.getBoolean(ballString.charAt(2)), GameScore.getBoolean(ballString.charAt(3)), GameScore.getBoolean(ballString.charAt(4))};
                    ballsOfGames.get(currentGame)[currentFrame][i] = ballBoolean;
                }
                String foulsOfFrame = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                for (int ballCounter = 0; ballCounter < 3; ballCounter++)
                {
                    if (foulsOfFrame.contains(String.valueOf(ballCounter + 1)))
                    {
                        foulsOfGames.get(currentGame)[currentFrame][ballCounter] = true;
                    }
                }

                currentFrame++;
                cursor.moveToNext();
            }
        }
        
        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(GAME_LARGE_FONT_SIZE);
        Paint paintBlackOutline = new Paint();
        paintBlackOutline.setColor(Color.BLACK);

        Bitmap bitmap = Bitmap.createBitmap(BITMAP_SERIES_GAME_NAME_WIDTH + BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT * gameIDs.length - gameIDs.length + 1, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        for (int i = 0; i < gameIDs.length; i++)
        {
            canvas.drawLine(0, BITMAP_GAME_HEIGHT * i - i, BITMAP_SERIES_GAME_NAME_WIDTH, BITMAP_GAME_HEIGHT * i - i, paintBlackOutline);
            canvas.drawText("Game " + (i + 1), 5, BITMAP_GAME_HEIGHT * i + GAME_LARGE_FONT_SIZE / 2 + BITMAP_GAME_HEIGHT / 2 - i, paintText);
            canvas.drawBitmap(createImageFromGame(ballsOfGames.get(i), foulsOfGames.get(i)), BITMAP_SERIES_GAME_NAME_WIDTH, BITMAP_GAME_HEIGHT * i - i, null);
        }

        canvas.drawLines(new float[]
                {0, 0, BITMAP_SERIES_GAME_NAME_WIDTH, 0,
                0, 0, 0, BITMAP_GAME_HEIGHT * gameIDs.length - gameIDs.length,
                0, BITMAP_GAME_HEIGHT * gameIDs.length - gameIDs.length, BITMAP_SERIES_GAME_NAME_WIDTH, BITMAP_GAME_HEIGHT * gameIDs.length - gameIDs.length}, paintBlackOutline);

        return bitmap;
    }
}

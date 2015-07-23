package ca.josephroque.bowlingcompanion.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by Joseph Roque on 15-03-26.
 * <p/>
 * Provides methods relating to creating images of the statistics managed by the application.
 */
public final class ImageUtils
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "ImageUtils";

    /**
     * Default private constructor.
     */
    private ImageUtils()
    {
        // does nothing
    }

    /** Width of a single game's frames. */
    private static final int BITMAP_GAME_WIDTH = 660;
    /** Height of a single game's row. */
    private static final int BITMAP_GAME_HEIGHT = 60;
    /** Height of a single ball cell. */
    private static final int BITMAP_GAME_BALL_HEIGHT = 20;
    /** Width of a single ball cell. */
    private static final int BITMAP_GAME_BALL_WIDTH = 20;
    /** Height of a single frame cell. */
    private static final int BITMAP_GAME_FRAME_HEIGHT = 40;
    /** Width of a single frame cell. */
    private static final int BITMAP_GAME_FRAME_WIDTH = 60;
    /** Width of the cell for the game's name. */
    private static final int BITMAP_SERIES_GAME_NAME_WIDTH = 80;

    /** Default font size for writing game data. */
    private static final float GAME_DEFAULT_FONT_SIZE = 12;
    /** Small font size for writing game data. */
    private static final float GAME_SMALL_FONT_SIZE = 8;
    /** Large font size for writing game data. */
    private static final float GAME_LARGE_FONT_SIZE = 16;

    /** Y position of text for ball data. */
    private static final float BALL_TEXT_Y = BITMAP_GAME_BALL_HEIGHT / 2
            + GAME_DEFAULT_FONT_SIZE / 2;

    /**
     * Creates an image, writing the game scores and individual ball values in a standard format.
     *
     * @param pinState state of the pins of each ball in each frame
     * @param fouls indicates whether a foul was invoked for each ball
     * @param gameScore final score of the game
     * @param isManual indicates if the score of the game was manually set
     * @return a formatted bitmap containing the game data
     */
    @SuppressWarnings("UnusedAssignment") //canvas set to null to free memory
    public static Bitmap createImageFromGame(boolean[][][] pinState,
                                             boolean[][] fouls,
                                             short gameScore,
                                             boolean isManual)
    {
        Bitmap bitmap =
                Bitmap.createBitmap(BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paintBlackOutline = new Paint();
        paintBlackOutline.setColor(Color.BLACK);

        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(GAME_DEFAULT_FONT_SIZE);

        int[] frameScores = new int[Constants.NUMBER_OF_FRAMES];
        int foulCount = 0;

        for (int frame = Constants.LAST_FRAME; frame >= 0; frame--)
        {
            final String[] ballString = new String[3];
            if (frame == Constants.LAST_FRAME) //Treat last frame differently than rest
            {
                if (Arrays.equals(pinState[frame][0], Constants.FRAME_PINS_DOWN))
                {
                    //If first ball is a strike, next two can be strikes/spares
                    ballString[0] = Constants.BALL_STRIKE;
                    if (Arrays.equals(pinState[frame][1], Constants.FRAME_PINS_DOWN))
                    {
                        ballString[1] = Constants.BALL_STRIKE;
                        ballString[2] = Score.getValueOfBall(pinState[frame][2], 2, true, false);
                    }
                    else
                    {
                        ballString[1] = Score.getValueOfBall(pinState[frame][1], 1, false, false);
                        if (Arrays.equals(pinState[frame][2], Constants.FRAME_PINS_DOWN))
                            ballString[2] = Constants.BALL_SPARE;
                        else
                            ballString[2] = Score.getValueOfBallDifference(
                                    pinState[frame], 2, false, false);
                    }
                }
                else
                {
                    //If first ball is not a strike, score is calculated normally
                    ballString[0] = Score.getValueOfBall(pinState[frame][0], 0, false, false);
                    if (Arrays.equals(pinState[frame][1], Constants.FRAME_PINS_DOWN))
                    {
                        ballString[1] = Constants.BALL_SPARE;
                        ballString[2] = Score.getValueOfBall(pinState[frame][2], 2, true, false);
                    }
                    else
                    {
                        ballString[1] =
                                Score.getValueOfBallDifference(pinState[frame], 1, false, false);
                        ballString[2] =
                                Score.getValueOfBallDifference(pinState[frame], 2, false, false);
                    }
                }
            }
            else
            {
                ballString[0] = Score.getValueOfBallDifference(pinState[frame], 0, false, false);
                if (!Arrays.equals(pinState[frame][0], Constants.FRAME_PINS_DOWN))
                {
                    if (Arrays.equals(pinState[frame][1], Constants.FRAME_PINS_DOWN))
                    {
                        ballString[1] = Constants.BALL_SPARE;
                        ballString[2] =
                                Score.getValueOfBallDifference(pinState[frame + 1], 0, false, true);
                    }
                    else
                    {
                        ballString[1] =
                                Score.getValueOfBallDifference(pinState[frame], 1, false, false);
                        ballString[2] =
                                Score.getValueOfBallDifference(pinState[frame], 2, false, false);
                    }
                }
                else
                {
                    ballString[1] =
                            Score.getValueOfBallDifference(pinState[frame + 1], 0, false, true);
                    if (Arrays.equals(pinState[frame + 1][0], Constants.FRAME_PINS_DOWN))
                    {
                        if (frame < Constants.LAST_FRAME - 1)
                        {
                            ballString[2] = Score.getValueOfBallDifference(
                                    pinState[frame + 2], 0, false, true);
                        }
                        else
                        {
                            ballString[2]
                                    = Score.getValueOfBall(pinState[frame + 1][1], 1, false, true);
                        }
                    }
                    else
                    {
                        ballString[2] =
                                Score.getValueOfBallDifference(pinState[frame + 1], 1, false, true);
                    }
                }
            }

            canvas.drawText(ballString[0], BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH
                    * frame, BALL_TEXT_Y, paintText);
            canvas.drawText(ballString[1], BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2
                    + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
            canvas.drawText(ballString[2], BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2
                    + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);

            paintText.setTextSize(GAME_SMALL_FONT_SIZE);
            for (int ball = 0; ball < pinState[frame].length; ball++)
            {
                if (fouls[frame][ball])
                {
                    foulCount++;
                    canvas.drawText("F", BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH
                            * frame + BITMAP_GAME_BALL_WIDTH / 2, BITMAP_GAME_BALL_HEIGHT
                            + GAME_SMALL_FONT_SIZE + 2, paintText);
                }
                canvas.drawLine(BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame,
                        0, BITMAP_GAME_BALL_WIDTH * ball + BITMAP_GAME_FRAME_WIDTH * frame,
                        BITMAP_GAME_BALL_HEIGHT, paintBlackOutline);
            }
            canvas.drawLine(BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_BALL_HEIGHT,
                    BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_FRAME_HEIGHT
                            + BITMAP_GAME_BALL_HEIGHT, paintBlackOutline);
            paintText.setTextSize(GAME_DEFAULT_FONT_SIZE);

            if (!isManual)
            {
                if (frame == Constants.LAST_FRAME)
                {
                    for (int b = 2; b >= 0; b--)
                    {
                        switch (b)
                        {
                            case 2:
                                frameScores[frame] += Score.getValueOfFrame(pinState[frame][b]);
                                break;
                            case 1:
                            case 0:
                                if (Arrays.equals(pinState[frame][b], Constants.FRAME_PINS_DOWN))
                                {
                                    frameScores[frame] += Score.getValueOfFrame(pinState[frame][b]);
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
                        if (b < 2 && Arrays.equals(pinState[frame][b], Constants.FRAME_PINS_DOWN))
                        {
                            frameScores[frame] += Score.getValueOfFrame(pinState[frame][b]);
                            frameScores[frame] += Score.getValueOfFrame(pinState[frame + 1][0]);
                            if (b == 0)
                            {
                                if (frame == Constants.LAST_FRAME - 1)
                                {
                                    if (frameScores[frame] == 30)
                                    {
                                        frameScores[frame] +=
                                                Score.getValueOfFrame(pinState[frame + 1][1]);
                                    }
                                    else
                                    {
                                        frameScores[frame] += Score.getValueOfFrameDifference(
                                                pinState[frame + 1][0], pinState[frame + 1][1]);
                                    }
                                }
                                else if (frameScores[frame] < 30)
                                {
                                    frameScores[frame] += Score.getValueOfFrameDifference(
                                            pinState[frame + 1][0], pinState[frame + 1][1]);
                                }
                                else
                                {
                                    frameScores[frame] += Score.getValueOfFrame(
                                            pinState[frame + 2][0]);
                                }
                            }
                            break;
                        }
                        else if (b == 2)
                        {
                            frameScores[frame] += Score.getValueOfFrame(pinState[frame][b]);
                        }
                    }
                }
            }
        }

        int totalScore = 0;
        paintText.setTextSize(GAME_LARGE_FONT_SIZE);
        for (int i = 0; i < frameScores.length; i++)
        {
            totalScore += frameScores[i];
            canvas.drawText((!isManual) ? String.valueOf(totalScore) : "--",
                    i * BITMAP_GAME_FRAME_WIDTH + BITMAP_GAME_FRAME_WIDTH / 2,
                    BITMAP_GAME_HEIGHT - 8,
                    paintText);
        }

        int scoreWithFouls = totalScore - 15 * foulCount;
        if (scoreWithFouls < 0)
            scoreWithFouls = 0;
        canvas.drawText((!isManual) ? String.valueOf(scoreWithFouls) : String.valueOf(gameScore),
                BITMAP_GAME_WIDTH - BITMAP_GAME_FRAME_WIDTH / 2,
                BITMAP_GAME_HEIGHT / 2 + GAME_LARGE_FONT_SIZE / 2,
                paintText);

        canvas.drawLines(new float[]{0, 0, BITMAP_GAME_WIDTH, 0,
                        0, 0, 0, BITMAP_GAME_HEIGHT,
                        0, BITMAP_GAME_HEIGHT - 1, BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT - 1,
                        BITMAP_GAME_WIDTH - 1, 0, BITMAP_GAME_WIDTH - 1, BITMAP_GAME_HEIGHT,
                        0, BITMAP_GAME_BALL_HEIGHT, BITMAP_GAME_WIDTH - BITMAP_GAME_FRAME_WIDTH, BITMAP_GAME_BALL_HEIGHT,
                        BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, 0, BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, BITMAP_GAME_HEIGHT},
                paintBlackOutline);

        canvas = null;
        return bitmap;
    }

    /**
     * Loads the data of each game in a series and creates a single image to.
     *
     * @param context context used to get an instance of the database
     * @param seriesId id of the series to load game from
     * @return an image of each games' data and score
     */
    @SuppressWarnings("UnusedAssignment") //canvas set to null to free memory
    public static Bitmap createImageFromSeries(Context context, long seriesId)
    {
        List<boolean[][][]> ballsOfGames = new ArrayList<>();
        List<boolean[][]> foulsOfGames = new ArrayList<>();
        List<Short> scoresOfGames = new ArrayList<>();
        List<Boolean> manualScores = new ArrayList<>();

        MainActivity.waitForSaveThreads(new WeakReference<>((MainActivity) context));

        SQLiteDatabase database = DatabaseHelper.getInstance(context).getReadableDatabase();
        String rawImageQuery = "SELECT "
                + GameEntry.COLUMN_GAME_NUMBER + ", "
                + GameEntry.COLUMN_SCORE + ", "
                + GameEntry.COLUMN_IS_MANUAL + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2] + ", "
                + FrameEntry.COLUMN_FOULS
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " INNER JOIN " + FrameEntry.TABLE_NAME
                + " ON game." + GameEntry._ID + "=" + FrameEntry.COLUMN_GAME_ID
                + " WHERE game." + GameEntry.COLUMN_SERIES_ID + "=?"
                + " ORDER BY " + GameEntry.COLUMN_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER;
        String[] rawImageArgs = new String[]{String.valueOf(seriesId)};
        Cursor cursor = database.rawQuery(rawImageQuery, rawImageArgs);

        int currentFrame = 0;
        int currentGame = -1;
        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                int frameNumber = cursor.getInt(cursor.getColumnIndex(
                        FrameEntry.COLUMN_FRAME_NUMBER));
                if (frameNumber == 1)
                {
                    currentFrame = 0;
                    currentGame++;
                    scoresOfGames.add(cursor.getShort(
                            cursor.getColumnIndex(GameEntry.COLUMN_SCORE)));
                    manualScores.add(cursor.getInt(
                            cursor.getColumnIndex(GameEntry.COLUMN_IS_MANUAL)) == 1);
                    ballsOfGames.add(new boolean[Constants.NUMBER_OF_FRAMES][3][5]);
                    foulsOfGames.add(new boolean[Constants.NUMBER_OF_FRAMES][3]);
                }

                for (int i = 0; i < 3; i++)
                {
                    String ballString = cursor.getString(
                            cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[i]));
                    boolean[] ballBoolean = {
                            Score.getBoolean(ballString.charAt(0)),
                            Score.getBoolean(ballString.charAt(1)),
                            Score.getBoolean(ballString.charAt(2)),
                            Score.getBoolean(ballString.charAt(3)),
                            Score.getBoolean(ballString.charAt(4))};
                    ballsOfGames.get(currentGame)[currentFrame][i] = ballBoolean;
                }
                String foulsOfFrame = cursor.getString(
                        cursor.getColumnIndex(FrameEntry.COLUMN_FOULS));
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
        cursor.close();
        cursor = null;

        final int numberOfGames = ballsOfGames.size();

        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(GAME_LARGE_FONT_SIZE);
        Paint paintBlackOutline = new Paint();
        paintBlackOutline.setColor(Color.BLACK);

        Bitmap bitmap = Bitmap.createBitmap(BITMAP_SERIES_GAME_NAME_WIDTH + BITMAP_GAME_WIDTH,
                (BITMAP_GAME_HEIGHT - 1) * numberOfGames + 1, Bitmap.Config.RGB_565);
        Bitmap gameBitmap;
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        for (int i = 0; i < numberOfGames; i++)
        {
            canvas.drawLine(0, BITMAP_GAME_HEIGHT * i - i, BITMAP_SERIES_GAME_NAME_WIDTH,
                    BITMAP_GAME_HEIGHT * i - i, paintBlackOutline);
            canvas.drawText("Game " + (i + 1), 5,
                    BITMAP_GAME_HEIGHT * i + GAME_LARGE_FONT_SIZE / 2 + BITMAP_GAME_HEIGHT / 2 - i,
                    paintText);

            gameBitmap = createImageFromGame(ballsOfGames.get(i), foulsOfGames.get(i),
                    scoresOfGames.get(i), manualScores.get(i));
            canvas.drawBitmap(gameBitmap, BITMAP_SERIES_GAME_NAME_WIDTH, BITMAP_GAME_HEIGHT * i - i,
                    null);
            gameBitmap.recycle();
            System.gc();
        }

        canvas.drawLines(new float[]
                {0, 0, BITMAP_SERIES_GAME_NAME_WIDTH, 0,
                        0, 0, 0, (BITMAP_GAME_HEIGHT - 1) * numberOfGames,
                        0, (BITMAP_GAME_HEIGHT - 1) * numberOfGames, BITMAP_SERIES_GAME_NAME_WIDTH, (BITMAP_GAME_HEIGHT - 1) * numberOfGames}, paintBlackOutline);
        canvas = null;

        return bitmap;
    }

    /**
     * A copy of the Android internals  insertImage method, this method populates the
     * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
     * that is inserted manually gets saved at the end of the gallery (because date is not
     * populated).
     *
     * @param cr n/a
     * @param source n/a
     * @param title n/a
     * @param description n/a
     * @return n/a
     *
     * @see android.provider.MediaStore.Images.Media#insertImage(android.content.ContentResolver,
     * Bitmap, String, String)
     */
    @SuppressWarnings("all")
    //Ignoring try with automatic resource management in case java 1.6 is used
    public static Uri insertImage(ContentResolver cr,
                                  Bitmap source,
                                  String title,
                                  String description)
    {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;

        try
        {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (source != null)
            {
                OutputStream imageOut = cr.openOutputStream(url);

                try
                {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                }
                finally
                {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            }
            else
            {
                cr.delete(url, null, null);
                url = null;
            }
        }
        catch (Exception e)
        {
            if (url != null)
            {
                cr.delete(url, null, null);
                url = null;
            }
        }

        return url;
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     *
     * @param cr n/a
     * @param source n/a
     * @param id n/a
     * @param width n/a
     * @param height n/a
     * @param kind n/a
     * @return n/a
     *
     * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
     */
    private static Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind)
    {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try
        {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        }
        catch (FileNotFoundException ex)
        {
            return null;
        }
        catch (IOException ex)
        {
            return null;
        }
    }
}

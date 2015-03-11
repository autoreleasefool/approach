package ca.josephroque.bowlingcompanion.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.GameActivity;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-03-02.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.data
 * in project Bowling Companion
 */
public class External
{

    private static final String TAG = "External";

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

    public static Intent getEmailIntent(
            String recipientEmail,
            String emailSubject,
            String emailBody)
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        return emailIntent;
    }

    public static Intent getEmailIntent(
            String recipientEmail,
            String emailSubject)
    {
        return getEmailIntent(recipientEmail, emailSubject, "");
    }

    public static Bitmap createImageFromGame(boolean[][][] pinState, boolean[][] fouls)
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
                if (Arrays.equals(pinState[frame][0], Constants.FRAME_PINS_DOWN))
                {
                    canvas.drawText(Constants.BALL_STRIKE, BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    if (Arrays.equals(pinState[frame][1], Constants.FRAME_PINS_DOWN))
                    {
                        canvas.drawText(Constants.BALL_STRIKE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBall(pinState[frame][2], 2, true), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBall(pinState[frame][1], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        if (Arrays.equals(pinState[frame][2], Constants.FRAME_PINS_DOWN))
                            canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        else
                            canvas.drawText(GameScore.getValueOfBallDifference(pinState[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                }
                else
                {
                    canvas.drawText(GameScore.getValueOfBall(pinState[frame][0], 0, false), BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    if (Arrays.equals(pinState[frame][1], Constants.FRAME_PINS_DOWN))
                    {
                        canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBall(pinState[frame][2], 2, true), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBallDifference(pinState[frame], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBallDifference(pinState[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                }
            }
            else
            {
                canvas.drawText(GameScore.getValueOfBallDifference(pinState[frame], 0, false), BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                if (!Arrays.equals(pinState[frame][0], Constants.FRAME_PINS_DOWN))
                {
                    if (Arrays.equals(pinState[frame][1], Constants.FRAME_PINS_DOWN))
                    {
                        canvas.drawText(Constants.BALL_SPARE, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                    else
                    {
                        canvas.drawText(GameScore.getValueOfBallDifference(pinState[frame], 1, false), BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                        canvas.drawText(GameScore.getValueOfBallDifference(pinState[frame], 2, false), BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    }
                }
                else
                {
                    canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                    canvas.drawText(Constants.BALL_EMPTY, BITMAP_GAME_BALL_WIDTH * 2 + BITMAP_GAME_BALL_WIDTH / 2 + BITMAP_GAME_FRAME_WIDTH * frame, BALL_TEXT_Y, paintText);
                }
            }

            paintText.setTextSize(GAME_SMALL_FONT_SIZE);
            for (int ball = 0; ball < pinState[frame].length; ball++)
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
                            frameScores[frame] += GameScore.getValueOfFrame(pinState[frame][b]);
                            break;
                        case 1:
                        case 0:
                            if (Arrays.equals(pinState[frame][b], Constants.FRAME_PINS_DOWN))
                            {
                                frameScores[frame] += GameScore.getValueOfFrame(pinState[frame][b]);
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
                        frameScores[frame] += GameScore.getValueOfFrame(pinState[frame][b]);
                        frameScores[frame] += GameScore.getValueOfFrame(pinState[frame + 1][0]);
                        if (b == 0)
                        {
                            if (frame == Constants.LAST_FRAME - 1)
                            {
                                if (frameScores[frame] == 30)
                                {
                                    frameScores[frame] += GameScore.getValueOfFrame(pinState[frame + 1][1]);
                                }
                                else
                                {
                                    frameScores[frame] += GameScore.getValueOfFrameDifference(pinState[frame + 1][0], pinState[frame + 1][1]);
                                }
                            }
                            else if (frameScores[frame] < 30)
                            {
                                frameScores[frame] += GameScore.getValueOfFrameDifference(pinState[frame + 1][0], pinState[frame + 1][1]);
                            }
                            else
                            {
                                frameScores[frame] += GameScore.getValueOfFrame(pinState[frame + 2][0]);
                            }
                        }
                        break;
                    }
                    else if (b == 2)
                    {
                        frameScores[frame] += GameScore.getValueOfFrame(pinState[frame][b]);
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

        canvas.drawLines(new float[]{0,0,BITMAP_GAME_WIDTH, 0,
                        0,0,0,BITMAP_GAME_HEIGHT,
                        0,BITMAP_GAME_HEIGHT - 1,BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT - 1,
                        BITMAP_GAME_WIDTH - 1, 0, BITMAP_GAME_WIDTH - 1, BITMAP_GAME_HEIGHT,
                        0, BITMAP_GAME_BALL_HEIGHT, BITMAP_GAME_WIDTH - BITMAP_GAME_FRAME_WIDTH, BITMAP_GAME_BALL_HEIGHT,
                        BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, 0, BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, BITMAP_GAME_HEIGHT},
                        paintBlackOutline);

        return bitmap;
    }

    public static Bitmap createImageFromSeries(Context context, long seriesId)
    {
        List<boolean[][][]> ballsOfGames = new ArrayList<>();
        List<boolean[][]> foulsOfGames = new ArrayList<>();

        SQLiteDatabase database = DatabaseHelper.getInstance(context).getReadableDatabase();
        String rawImageQuery = "SELECT "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2] + ", "
                + FrameEntry.COLUMN_NAME_FOULS
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " LEFT JOIN " + FrameEntry.TABLE_NAME
                + " ON game." + GameEntry._ID + "=" + FrameEntry.COLUMN_NAME_GAME_ID
                + " WHERE game." + GameEntry.COLUMN_NAME_SERIES_ID + "=?"
                + " ORDER BY " + GameEntry.COLUMN_NAME_GAME_NUMBER + ", " + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawImageArgs = new String[]{String.valueOf(seriesId)};
        Cursor cursor = database.rawQuery(rawImageQuery, rawImageArgs);

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

        final int numberOfGames = ballsOfGames.size();

        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(GAME_LARGE_FONT_SIZE);
        Paint paintBlackOutline = new Paint();
        paintBlackOutline.setColor(Color.BLACK);

        Bitmap bitmap = Bitmap.createBitmap(BITMAP_SERIES_GAME_NAME_WIDTH + BITMAP_GAME_WIDTH, (BITMAP_GAME_HEIGHT - 1) * numberOfGames + 1, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        for (int i = 0; i < numberOfGames; i++)
        {
            canvas.drawLine(0, BITMAP_GAME_HEIGHT * i - i, BITMAP_SERIES_GAME_NAME_WIDTH, BITMAP_GAME_HEIGHT * i - i, paintBlackOutline);
            canvas.drawText("Game " + (i + 1), 5, BITMAP_GAME_HEIGHT * i + GAME_LARGE_FONT_SIZE / 2 + BITMAP_GAME_HEIGHT / 2 - i, paintText);
            canvas.drawBitmap(createImageFromGame(ballsOfGames.get(i), foulsOfGames.get(i)), BITMAP_SERIES_GAME_NAME_WIDTH, BITMAP_GAME_HEIGHT * i - i, null);
        }

        canvas.drawLines(new float[]
                {0, 0, BITMAP_SERIES_GAME_NAME_WIDTH, 0,
                        0, 0, 0, (BITMAP_GAME_HEIGHT - 1) * numberOfGames,
                        0, (BITMAP_GAME_HEIGHT - 1) * numberOfGames, BITMAP_SERIES_GAME_NAME_WIDTH, (BITMAP_GAME_HEIGHT - 1) * numberOfGames}, paintBlackOutline);

        return bitmap;
    }

    public static void showShareDialog(final Activity activity, final long seriesId)
    {
        final CharSequence[] options = {"Save", "Share"};
        AlertDialog.Builder shareBuilder = new AlertDialog.Builder(activity);
        shareBuilder.setTitle("Save to device or share?")
                .setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0)
                            saveSeriesToDevice(activity, seriesId);
                        else if (which == 1)
                            shareSeries(activity, seriesId);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(Constants.DIALOG_CANCEL, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private static void shareSeries(Activity activity, long seriesId)
    {
        new ShareSeriesTask().execute(activity, seriesId);
    }

    private static void saveSeriesToDevice(final Activity activity, final long seriesId)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final Uri imageUri = CapturePhotoUtils.insertImage(activity.getContentResolver(),
                        createImageFromSeries(activity, seriesId),
                        String.valueOf(System.currentTimeMillis()),
                        "Series: " + seriesId);

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        MediaScannerConnection.scanFile(activity,
                                new String[]{imageUri.getPath()}, null,
                                new MediaScannerConnection.OnScanCompletedListener()
                                {
                                    public void onScanCompleted(String path, Uri uri)
                                    {
                                        Log.i("ExternalStorage", "Scanned " + path + ":");
                                        Log.i("ExternalStorage", "-> uri=" + uri);
                                    }
                                });

                        Toast toast;
                        if (imageUri != null)
                            toast = Toast.makeText(activity, "Image successfully saved!", Toast.LENGTH_SHORT);
                        else
                            toast = Toast.makeText(activity, "Unable to save image", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }).start();
    }

    private static class ShareSeriesTask extends AsyncTask<Object, Void, Object[]>
    {
        @Override
        public Object[] doInBackground(Object... params)
        {
            Activity activity = (Activity)params[0];
            long seriesId = (Long)params[1];
            Bitmap image = createImageFromSeries(activity, seriesId);
            Bitmap icon = image;
            Uri imageUri = CapturePhotoUtils.insertImage(activity.getContentResolver(),
                    image,
                    String.valueOf(System.currentTimeMillis()),
                    "Series: " + seriesId);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            OutputStream outstream = null;
            try
            {
                outstream = activity.getContentResolver()
                        .openOutputStream(imageUri);
                icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            }
            catch (Exception e)
            {
                Log.w(TAG, "Unable to create stream from image");
            }
            finally
            {
                if (outstream != null)
                {
                    try
                    {
                        outstream.close();
                    }
                    catch (IOException ex){}
                }
            }

            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            return new Object[]{activity, shareIntent};
        }

        @Override
        public void onPostExecute(Object[] params)
        {
            GameActivity activity = (GameActivity)params[0];
            Intent shareIntent = (Intent)params[1];

            activity.startActivity(Intent.createChooser(shareIntent, "Share Image"));
        }
    }
}

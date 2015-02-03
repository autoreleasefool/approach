package ca.josephroque.bowlingcompanion.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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

        for (int frame = 0; frame < ballsOfFrame.length; frame++)
        {
            for (int ball = 0; ball < ballsOfFrame[frame].length; ball++)
            {
                for (int pin = 0; pin < ballsOfFrame[frame][ball].length; pin++)
                {
                }
                canvas.drawLine(BITMAP_GAME_BALL_WIDTH * ball, 0, BITMAP_GAME_BALL_WIDTH * ball, BITMAP_GAME_BALL_HEIGHT, paintBlackOutline);
            }

            canvas.drawLine(BITMAP_GAME_FRAME_WIDTH * frame, 0, BITMAP_GAME_FRAME_WIDTH * frame, BITMAP_GAME_FRAME_HEIGHT, paintBlackOutline);
        }

        canvas.drawLines(new float[]
                        {0,0,BITMAP_GAME_WIDTH, 0,
                        0,0,0,BITMAP_GAME_HEIGHT,
                        0,BITMAP_GAME_HEIGHT - 1,BITMAP_GAME_WIDTH, BITMAP_GAME_HEIGHT - 1,
                        BITMAP_GAME_WIDTH - 1, 0, BITMAP_GAME_WIDTH - 1, BITMAP_GAME_HEIGHT,
                        0, BITMAP_GAME_BALL_HEIGHT, BITMAP_GAME_WIDTH, BITMAP_GAME_BALL_HEIGHT,
                        BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, 0, BITMAP_GAME_FRAME_WIDTH * Constants.NUMBER_OF_FRAMES, BITMAP_GAME_HEIGHT},
                paintBlackOutline);

        return bitmap;
    }
}

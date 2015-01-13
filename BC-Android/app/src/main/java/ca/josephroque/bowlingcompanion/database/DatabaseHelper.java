package ca.josephroque.bowlingcompanion.database;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by josephroque on 15-01-09.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "bowlingdata";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase database;

    private static DatabaseHelper dbHelperInstance = null;

    public static DatabaseHelper getInstance(Context context)
    {
        if (dbHelperInstance == null)
        {
            dbHelperInstance = new DatabaseHelper(context);
        }
        return dbHelperInstance;
    }

    public static void closeInstance()
    {
        if (dbHelperInstance != null)
        {
            dbHelperInstance.close();
        }
    }

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        database = db;

        database.execSQL("CREATE TABLE " + BowlerEntry.TABLE_NAME + " ("
                + BowlerEntry._ID + " INTEGER PRIMARY KEY, "
                + BowlerEntry.COLUMN_NAME_BOWLER_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + BowlerEntry.COLUMN_NAME_DATE_MODIFIED + " TEXT NOT NULL"
                + ");");
        database.execSQL("CREATE TABLE " + LeagueEntry.TABLE_NAME + " ("
                + LeagueEntry._ID + " INTEGER PRIMARY KEY, "
                + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + " INTEGER DEFAULT 3, "
                + LeagueEntry.COLUMN_NAME_DATE_MODIFIED + " TEXT NOT NULL, "
                + LeagueEntry.COLUMN_NAME_BOWLER_ID + " INTEGER NOT NULL"
                /*
                Not using foreign key syntax - unsure if accepted by SQLite
                "FOREIGN KEY(" + LeagueEntry.COLUMN_NAME_BOWLER_ID + ") REFERENCES " + BowlerEntry.TABLE_NAME + "(" + BowlerEntry._ID + ")" +*/
                + ");");
        database.execSQL("CREATE TABLE " + SeriesEntry.TABLE_NAME + " ("
                + SeriesEntry._ID + " INTEGER PRIMARY KEY, "
                + SeriesEntry.COLUMN_NAME_DATE_CREATED + " TEXT NOT NULL, "
                + SeriesEntry.COLUMN_NAME_LEAGUE_ID + " INTEGER NOT NULL, "
                + SeriesEntry.COLUMN_NAME_BOWLER_ID + " INTEGER NOT NULL"
                /*
                Not using foreign key syntax - unsure if accepted by SQLite
                "FOREIGN KEY(" + SeriesEntry.COLUMN_NAME_LEAGUE_ID + ") REFERENCES " + LeagueEntry.TABLE_NAME + "(" + LeagueEntry._ID + ")" + */
                + ");");
        database.execSQL("CREATE TABLE " + GameEntry.TABLE_NAME + " ("
                + GameEntry._ID + " INTEGER PRIMARY KEY, "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + " INTEGER NOT NULL, "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + " INTEGER DEFAULT 0, "
                + GameEntry.COLUMN_NAME_SERIES_ID + " INTEGER NOT NULL, "
                + GameEntry.COLUMN_NAME_BOWLER_ID + " INTEGER NOT NULL"
                + ");");
        database.execSQL("CREATE TABLE " + FrameEntry.TABLE_NAME + " ("
                + FrameEntry._ID + " INTEGER PRIMARY KEY, "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_NAME_BALL[0] + " TEXT DEFAULT 'ooooo', "
                + FrameEntry.COLUMN_NAME_BALL[1] + " TEXT DEFAULT 'ooooo', "
                + FrameEntry.COLUMN_NAME_BALL[2] + " TEXT DEFAULT 'ooooo', "
                + FrameEntry.COLUMN_NAME_GAME_ID + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_NAME_SERIES_ID + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_NAME_LEAGUE_ID + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_NAME_BOWLER_ID + " INTEGER NOT NULL"
                /*
                Not using foreign key syntax - unsure if accepted by SQLite
                "FOREIGN KEY(" + FrameEntry.COLUMN_NAME_SERIES_ID + ") REFERENCES " + SeriesEntry.TABLE_NAME + "(" + SeriesEntry._ID + ")," +
                "FOREIGN KEY(" + FrameEntry.COLUMN_NAME_LEAGUE_ID + ") REFERENCES " + LeagueEntry.TABLE_NAME + "(" + LeagueEntry._ID + ")," +
                "FOREIGN KEY(" + FrameEntry.COLUMN_NAME_BOWLER_ID + ") REFERENCES " + BowlerEntry.TABLE_NAME + "(" + BowlerEntry._ID + ")" +*/
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + BowlerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LeagueEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SeriesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FrameEntry.TABLE_NAME);
        onCreate(db);
    }
}

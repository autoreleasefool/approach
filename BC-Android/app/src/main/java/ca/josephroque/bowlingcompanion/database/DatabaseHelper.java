package ca.josephroque.bowlingcompanion.database;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by josephroque on 15-01-09.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.database
 * in project Bowling Companion
 */
public class DatabaseHelper extends SQLiteOpenHelper
{

    /** Tag for the database to be used in log output */
    private static final String TAG = "DBHelper";
    /** Name of the database */
    private static final String DATABASE_NAME = "bowlingdata";
    /** Version of the database, incremented with changes */
    private static final int DATABASE_VERSION = 1;

    /** Instance of the database */
    private SQLiteDatabase database;

    /** Singleton instance of the DatabaseHelper */
    private static DatabaseHelper dbHelperInstance = null;

    /**
     * Returns a singleton instance of DatabaseHelper
     *
     * @param context The current activity
     * @return dbHelperInstance
     */
    public static DatabaseHelper getInstance(Context context)
    {
        if (dbHelperInstance == null)
        {
            dbHelperInstance = new DatabaseHelper(context);
        }
        return dbHelperInstance;
    }

    /**
     * Closes the current instance of the database helper
     */
    public static void closeInstance()
    {
        if (dbHelperInstance != null)
        {
            dbHelperInstance.close();
        }
    }

    /**
     * Private constructor for singleton access
     *
     * @param context The current activity
     */
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
                + ");");
        database.execSQL("CREATE TABLE " + SeriesEntry.TABLE_NAME + " ("
                + SeriesEntry._ID + " INTEGER PRIMARY KEY, "
                + SeriesEntry.COLUMN_NAME_DATE_CREATED + " TEXT NOT NULL, "
                + SeriesEntry.COLUMN_NAME_LEAGUE_ID + " INTEGER NOT NULL, "
                + SeriesEntry.COLUMN_NAME_BOWLER_ID + " INTEGER NOT NULL"
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
                + FrameEntry.COLUMN_NAME_BALL[0] + " TEXT DEFAULT '00000', "
                + FrameEntry.COLUMN_NAME_BALL[1] + " TEXT DEFAULT '00000', "
                + FrameEntry.COLUMN_NAME_BALL[2] + " TEXT DEFAULT '00000', "
                + FrameEntry.COLUMN_NAME_FOULS + " INTEGER DEFAULT 0, "
                + FrameEntry.COLUMN_NAME_GAME_ID + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_NAME_SERIES_ID + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_NAME_LEAGUE_ID + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_NAME_BOWLER_ID + " INTEGER NOT NULL"
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

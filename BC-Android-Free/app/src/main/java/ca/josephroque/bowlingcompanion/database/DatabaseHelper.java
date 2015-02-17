package ca.josephroque.bowlingcompanion.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ca.josephroque.bowlingcompanion.database.Contract.*;

/**
 * Created by josephroque on 15-02-16.
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
    private SQLiteDatabase mDatabase;

    /** Singleton instance of the DatabaseHelper */
    private static DatabaseHelper sDatabaseHelperInstance = null;

    /**
     * Returns a singleton instance of DatabaseHelper
     *
     * @param context The current activity
     * @return dbHelperInstance
     */
    public static DatabaseHelper getInstance(Context context)
    {
        if (sDatabaseHelperInstance == null)
        {
            sDatabaseHelperInstance = new DatabaseHelper(context);
        }
        return sDatabaseHelperInstance;
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
        mDatabase = db;

        mDatabase.execSQL("CREATE TABLE " + BowlerEntry.TABLE_NAME + " ("
                + BowlerEntry._ID + " INTEGER PRIMARY KEY, "
                + BowlerEntry.COLUMN_NAME_BOWLER_NAME + " TEXT COLLATE NOCASE, "
                + BowlerEntry.COLUMN_NAME_DATE_MODIFIED + " TEXT"
                + ");");
        mDatabase.execSQL("CREATE TABLE " + LeagueEntry.TABLE_NAME + " ("
                + LeagueEntry._ID + " INTEGER PRIMARY KEY, "
                + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + " TEXT COLLATE NOCASE, "
                + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + " INTEGER DEFAULT 3, "
                + LeagueEntry.COLUMN_NAME_DATE_MODIFIED + " TEXT, "
                + LeagueEntry.COLUMN_NAME_IS_TOURNAMENT + " INTEGER DEFAULT 0, "
                + LeagueEntry.COLUMN_NAME_BOWLER_ID + " INTEGER"
                + ");");
        mDatabase.execSQL("CREATE TABLE " + SeriesEntry.TABLE_NAME + " ("
                + SeriesEntry._ID + " INTEGER PRIMARY KEY, "
                + SeriesEntry.COLUMN_NAME_DATE_CREATED + " TEXT, "
                + SeriesEntry.COLUMN_NAME_LEAGUE_ID + " INTEGER, "
                + SeriesEntry.COLUMN_NAME_BOWLER_ID + " INTEGER"
                + ");");
        mDatabase.execSQL("CREATE TABLE " + GameEntry.TABLE_NAME + " ("
                + GameEntry._ID + " INTEGER PRIMARY KEY, "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + " INTEGER, "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + " INTEGER DEFAULT 0, "
                + GameEntry.COLUMN_NAME_LEAGUE_ID + " INTEGER, "
                + GameEntry.COLUMN_NAME_SERIES_ID + " INTEGER, "
                + GameEntry.COLUMN_NAME_BOWLER_ID + " INTEGER"
                + ");");
        mDatabase.execSQL("CREATE TABLE " + FrameEntry.TABLE_NAME + " ("
                + FrameEntry._ID + " INTEGER PRIMARY KEY, "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + " INTEGER, "
                + FrameEntry.COLUMN_NAME_FRAME_ACCESSED + " INTEGER DEFAULT 0, "
                + FrameEntry.COLUMN_NAME_BALL[0] + " TEXT DEFAULT '00000', "
                + FrameEntry.COLUMN_NAME_BALL[1] + " TEXT DEFAULT '00000', "
                + FrameEntry.COLUMN_NAME_BALL[2] + " TEXT DEFAULT '00000', "
                + FrameEntry.COLUMN_NAME_FOULS + " TEXT DEFAULT '0', "
                + FrameEntry.COLUMN_NAME_GAME_ID + " INTEGER, "
                + FrameEntry.COLUMN_NAME_LEAGUE_ID + " INTEGER, "
                + FrameEntry.COLUMN_NAME_BOWLER_ID + " INTEGER"
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

    /**
     * Displays a dialog to the user to delete data in the database
     *
     * @param context the Activity context
     *                @param deleter
     * @param position selected bowler from list view
     * @param secondChance if false, will show a second dialog to confirm option. If
     *                     true, selecting 'delete' will delete all data of bowler
     */
    /**
     * Displays a dialog to the user to delete data in the database
     *
     * @param context the Activity context
     * @param deleter interface which should be overridden to call relevant method
     * @param name identifier for data to be deleted
     */
    public static void deleteData(final Activity context, final DataDeleter deleter, final String name)
    {
        AlertDialog.Builder mDeleteBuilder = new AlertDialog.Builder(context);
        mDeleteBuilder.setMessage("WARNING: This action cannot be undone! Delete all data for " + name + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleter.execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
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

    public static interface DataDeleter
    {
        public void execute();
    }
}
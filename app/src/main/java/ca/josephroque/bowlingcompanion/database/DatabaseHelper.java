package ca.josephroque.bowlingcompanion.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry;
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.Contract.MatchPlayEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.utilities.Score;

/**
 * Created by Joseph Roque on 15-03-12. Manages interactions with the application's database, including the creation,
 * updates and deletion.
 */
public final class DatabaseHelper
        extends SQLiteOpenHelper {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "DBHelper";

    /** Name of the database. */
    private static final String DATABASE_NAME = "bowlingdata";
    /** Version of the database, incremented with changes. */
    private static final int DATABASE_VERSION = 4;

    /** Singleton instance of the DatabaseHelper. */
    private static DatabaseHelper sDatabaseHelperInstance = null;

    /**
     * Returns a singleton instance of DatabaseHelper.
     *
     * @param context the current activity
     * @return static instance of DatabaseHelper
     */
    public static DatabaseHelper getInstance(Context context) {
        if (sDatabaseHelperInstance == null) {
            sDatabaseHelperInstance = new DatabaseHelper(context);
        }
        return sDatabaseHelperInstance;
    }

    /**
     * Private constructor for singleton access.
     *
     * @param context the current activity
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Defines tables for the database, creating columns and constraints
        createBowlerTable(db);
        createLeagueTable(db);
        createSeriesTable(db);
        createGameTable(db);
        createFrameTable(db);
        createMatchPlayTable(db);
        createTableIndices(db);
    }

    /**
     * Creates indices on the tables for the most commonly used columns for sorting and comparing.
     *
     * @param db database
     */
    private void createTableIndices(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX bowler_id_index ON "
                + BowlerEntry.TABLE_NAME + "(" + BowlerEntry._ID + ")");
        db.execSQL("CREATE INDEX league_id_index ON "
                + LeagueEntry.TABLE_NAME + "(" + LeagueEntry._ID + ")");
        db.execSQL("CREATE INDEX series_id_index ON "
                + SeriesEntry.TABLE_NAME + "(" + SeriesEntry._ID + ")");
        db.execSQL("CREATE INDEX game_id_index ON "
                + GameEntry.TABLE_NAME + "(" + GameEntry._ID + ")");
        db.execSQL("CREATE INDEX frame_id_index ON "
                + FrameEntry.TABLE_NAME + "(" + FrameEntry._ID + ")");
        db.execSQL("CREATE INDEX match_id_index ON "
                + MatchPlayEntry.TABLE_NAME + "(" + MatchPlayEntry._ID + ")");

        db.execSQL("CREATE INDEX league_bowler_fk_index ON "
                + LeagueEntry.TABLE_NAME + "(" + LeagueEntry.COLUMN_BOWLER_ID + ")");
        db.execSQL("CREATE INDEX series_league_fk_index ON "
                + SeriesEntry.TABLE_NAME + "(" + SeriesEntry.COLUMN_LEAGUE_ID + ")");
        db.execSQL("CREATE INDEX game_series_fk_index ON "
                + GameEntry.TABLE_NAME + "(" + GameEntry.COLUMN_SERIES_ID + ")");
        db.execSQL("CREATE INDEX frame_game_fk_index ON "
                + FrameEntry.TABLE_NAME + "(" + FrameEntry.COLUMN_GAME_ID + ")");
        db.execSQL("CREATE INDEX match_game_fk_index ON "
                + MatchPlayEntry.TABLE_NAME + "(" + MatchPlayEntry.COLUMN_GAME_ID + ")");
    }

    /**
     * Executes SQL statement to create table to store frames. Must be executed after {@code createGameTable}.
     *
     * @param db database
     */
    private void createFrameTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + FrameEntry.TABLE_NAME + " ("
                + FrameEntry._ID + " INTEGER PRIMARY KEY, "
                + FrameEntry.COLUMN_FRAME_NUMBER + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_IS_ACCESSED + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[0] + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[1] + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[2] + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_FOULS + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL"
                + " REFERENCES " + GameEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + FrameEntry.COLUMN_FRAME_NUMBER + " >= 1 AND "
                + FrameEntry.COLUMN_FRAME_NUMBER + " <= 10), "
                + "CHECK (" + FrameEntry.COLUMN_IS_ACCESSED + " = 0 OR "
                + FrameEntry.COLUMN_IS_ACCESSED + " = 1)"
                + ");");
    }

    /**
     * Executes SQL statement to create table to store games. Must be executed after {@code createSeriesTable}.
     *
     * @param db database
     */
    private void createGameTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + GameEntry.TABLE_NAME + " ("
                + GameEntry._ID + " INTEGER PRIMARY KEY, "
                + GameEntry.COLUMN_GAME_NUMBER + " INTEGER NOT NULL, "
                + GameEntry.COLUMN_SCORE + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_IS_MANUAL + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_IS_LOCKED + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_MATCH_PLAY + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_SERIES_ID + " INTEGER NOT NULL"
                + " REFERENCES " + SeriesEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + GameEntry.COLUMN_GAME_NUMBER + " >= 1 AND "
                + GameEntry.COLUMN_GAME_NUMBER + " <= 20), "
                + "CHECK (" + GameEntry.COLUMN_IS_LOCKED + " = 0 OR "
                + GameEntry.COLUMN_IS_LOCKED + " = 1), "
                + "CHECK (" + GameEntry.COLUMN_IS_MANUAL + " = 0 OR "
                + GameEntry.COLUMN_IS_MANUAL + " = 1), "
                + "CHECK (" + GameEntry.COLUMN_SCORE + " >= 0 AND "
                + GameEntry.COLUMN_SCORE + " <= 450), "
                + "CHECK (" + GameEntry.COLUMN_MATCH_PLAY + " >= 0 AND "
                + GameEntry.COLUMN_MATCH_PLAY + " <= 3)"
                + ");");
    }

    /**
     * Executes SQL statement to create table to store series. Must be executed after {@code createLeagueTable}.
     *
     * @param db database
     */
    private void createSeriesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + SeriesEntry.TABLE_NAME + " ("
                + SeriesEntry._ID + " INTEGER PRIMARY KEY, "
                + SeriesEntry.COLUMN_SERIES_DATE + " TEXT NOT NULL, "
                + SeriesEntry.COLUMN_LEAGUE_ID + " INTEGER NOT NULL"
                + " REFERENCES " + LeagueEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE"
                + ");");
    }

    /**
     * Executes SQL statement to create table to store leagues. Must be executed after {@code createBowlerTable}.
     *
     * @param db database
     */
    private void createLeagueTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + LeagueEntry.TABLE_NAME + "("
                + LeagueEntry._ID + " INTEGER PRIMARY KEY, "
                + LeagueEntry.COLUMN_LEAGUE_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " INTEGER NOT NULL, "
                + LeagueEntry.COLUMN_DATE_MODIFIED + " TEXT NOT NULL, "
                + LeagueEntry.COLUMN_IS_EVENT + " INTEGER NOT NULL DEFAULT 0, "
                + LeagueEntry.COLUMN_BOWLER_ID + " INTEGER NOT NULL"
                + " REFERENCES " + BowlerEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " > 0 AND "
                + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " <= 20), "
                + "CHECK (" + LeagueEntry.COLUMN_IS_EVENT + " = 0 OR "
                + LeagueEntry.COLUMN_IS_EVENT + " = 1)"
                + ");");
    }

    /**
     * Executes SQL statement to create table to store bowlers.
     *
     * @param db database
     */
    private void createBowlerTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + BowlerEntry.TABLE_NAME + "("
                + BowlerEntry._ID + " INTEGER PRIMARY KEY, "
                + BowlerEntry.COLUMN_BOWLER_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + BowlerEntry.COLUMN_DATE_MODIFIED + " TEXT NOT NULL"
                + ");");
    }

    /**
     * Executes SQL statement to create table to store match play results.
     *
     * @param db database
     */
    private void createMatchPlayTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + MatchPlayEntry.TABLE_NAME + "("
                + MatchPlayEntry._ID + " INTEGER PRIMARY KEY, "
                + MatchPlayEntry.COLUMN_OPPONENT_NAME + " TEXT COLLATE NOCASE, "
                + MatchPlayEntry.COLUMN_OPPONENT_SCORE + " INTEGER NOT NULL DEFAULT 0, "
                + MatchPlayEntry.COLUMN_GAME_ID + " INTEGER NOT NULL"
                + " REFERENCES " + GameEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + MatchPlayEntry.COLUMN_OPPONENT_SCORE + " >= 0 AND "
                + MatchPlayEntry.COLUMN_OPPONENT_SCORE + " <= 450)"
                + ");");
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * If an older version of the database exists, all the tables and data are dropped
         * and the table is recreated.
         *
         * In future version, if database is updated then tables should be altered,
         * not dropped
         */
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeDatabaseFrom1To2(db);
                    break;
                case 3:
                    upgradeDatabaseFrom2To3(db);
                    break;
                case 4:
                    upgradeDatabaseFrom3To4(db);
                    break;
                default:
                    dropTablesAndRecreate(db);
            }
            upgradeTo++;
        }
    }

    /**
     * Drops all tables in the database and calls onCreate().
     *
     * @param db database to wipe
     */
    private void dropTablesAndRecreate(SQLiteDatabase db) {
        Log.i(TAG, "Dropping tables");
        db.execSQL("DROP INDEX IF EXISTS bowler_id_index");
        db.execSQL("DROP INDEX IF EXISTS league_id_index");
        db.execSQL("DROP INDEX IF EXISTS series_id_index");
        db.execSQL("DROP INDEX IF EXISTS game_id_index");
        db.execSQL("DROP INDEX IF EXISTS frame_id_index");
        db.execSQL("DROP INDEX IF EXISTS match_id_index");

        db.execSQL("DROP INDEX IF EXISTS league_bowler_fk_index");
        db.execSQL("DROP INDEX IF EXISTS series_league_fk_index");
        db.execSQL("DROP INDEX IF EXISTS game_series_fk_index");
        db.execSQL("DROP INDEX IF EXISTS frame_game_fk_index");
        db.execSQL("DROP INDEX IF EXISTS match_game_fk_index");

        db.execSQL("DROP TABLE IF EXISTS " + MatchPlayEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FrameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SeriesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LeagueEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BowlerEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Upgrades database from oldVersion 1 to newVersion 2.
     *
     * @param db to upgrade
     */
    private void upgradeDatabaseFrom1To2(SQLiteDatabase db) {
        //Removes foreign key and check constraints from frame table
        db.execSQL("CREATE TABLE frame2 (" + FrameEntry._ID + " INTEGER PRIMARY KEY, "
                + FrameEntry.COLUMN_FRAME_NUMBER + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_IS_ACCESSED + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[0] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_PIN_STATE[1] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_PIN_STATE[2] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_FOULS + " TEXT NOT NULL DEFAULT '0', "
                + FrameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL"
                + ");");
        db.execSQL("INSERT INTO frame2 ("
                + FrameEntry._ID + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2] + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_GAME_ID + ")"
                + " SELECT "
                + FrameEntry._ID + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2] + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_GAME_ID + " FROM " + FrameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE " + FrameEntry.TABLE_NAME);
        db.execSQL("ALTER TABLE frame2 RENAME TO " + FrameEntry.TABLE_NAME);

        //Adds new column and check constraints to game table
        db.execSQL("CREATE TABLE game2 ("
                + GameEntry._ID + " INTEGER PRIMARY KEY, "
                + GameEntry.COLUMN_GAME_NUMBER + " INTEGER NOT NULL, "
                + GameEntry.COLUMN_SCORE + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_IS_MANUAL + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_IS_LOCKED + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_MATCH_PLAY + " INTEGER NOT NULL DEFAULT 0, "
                + GameEntry.COLUMN_SERIES_ID + " INTEGER NOT NULL"
                + " REFERENCES " + SeriesEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + GameEntry.COLUMN_GAME_NUMBER + " >= 1 AND "
                + GameEntry.COLUMN_GAME_NUMBER + " <= 20), "
                + "CHECK (" + GameEntry.COLUMN_IS_LOCKED + " = 0 OR "
                + GameEntry.COLUMN_IS_LOCKED + " = 1), "
                + "CHECK (" + GameEntry.COLUMN_IS_MANUAL + " = 0 OR "
                + GameEntry.COLUMN_IS_MANUAL + " = 1), "
                + "CHECK (" + GameEntry.COLUMN_SCORE + " >= 0 AND "
                + GameEntry.COLUMN_SCORE + " <= 450), "
                + "CHECK (" + GameEntry.COLUMN_MATCH_PLAY + " >= 0 AND "
                + GameEntry.COLUMN_MATCH_PLAY + " <= 3)"
                + ");");
        db.execSQL("INSERT INTO game2 ("
                + GameEntry._ID + ", "
                + GameEntry.COLUMN_GAME_NUMBER + ", "
                + GameEntry.COLUMN_SCORE + ", "
                + GameEntry.COLUMN_IS_MANUAL + ", "
                + GameEntry.COLUMN_IS_LOCKED + ", "
                + GameEntry.COLUMN_SERIES_ID + ")"
                + " SELECT * FROM " + GameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE " + GameEntry.TABLE_NAME);
        db.execSQL("ALTER TABLE game2 RENAME TO " + GameEntry.TABLE_NAME);

        //Adds foreign key and check constraints to frame table
        db.execSQL("CREATE TABLE frame2 ("
                + FrameEntry._ID + " INTEGER PRIMARY KEY, "
                + FrameEntry.COLUMN_FRAME_NUMBER + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_IS_ACCESSED + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[0] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_PIN_STATE[1] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_PIN_STATE[2] + " TEXT NOT NULL DEFAULT '00000', "
                + FrameEntry.COLUMN_FOULS + " TEXT NOT NULL DEFAULT '0', "
                + FrameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL"
                + " REFERENCES " + GameEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + FrameEntry.COLUMN_FRAME_NUMBER + " >= 1 AND "
                + FrameEntry.COLUMN_FRAME_NUMBER + " <= 10), "
                + "CHECK (" + FrameEntry.COLUMN_IS_ACCESSED + " = 0 OR "
                + FrameEntry.COLUMN_IS_ACCESSED + " = 1)"
                + ");");
        db.execSQL("INSERT INTO frame2 ("
                + FrameEntry._ID + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2] + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_GAME_ID + ")"
                + " SELECT "
                + FrameEntry._ID + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2] + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_GAME_ID + " FROM " + FrameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE " + FrameEntry.TABLE_NAME);
        db.execSQL("ALTER TABLE frame2 RENAME TO " + FrameEntry.TABLE_NAME);

        db.execSQL("CREATE INDEX game_id_index ON "
                + GameEntry.TABLE_NAME + "(" + GameEntry._ID + ")");
        db.execSQL("CREATE INDEX frame_id_index ON "
                + FrameEntry.TABLE_NAME + "(" + FrameEntry._ID + ")");
        db.execSQL("CREATE INDEX game_series_fk_index ON "
                + GameEntry.TABLE_NAME + "(" + GameEntry.COLUMN_SERIES_ID + ")");
        db.execSQL("CREATE INDEX frame_game_fk_index ON "
                + FrameEntry.TABLE_NAME + "(" + FrameEntry.COLUMN_GAME_ID + ")");
    }

    /**
     * Upgrades database from oldVersion 2 to newVersion 3.
     *
     * @param db to upgrade
     */
    private void upgradeDatabaseFrom2To3(SQLiteDatabase db) {
        db.execSQL("DROP INDEX IF EXISTS frame_id_index");
        db.execSQL("DROP INDEX IF EXISTS frame_game_fk_index");

        db.execSQL("CREATE TABLE frame2 ("
                + FrameEntry._ID + " INTEGER PRIMARY KEY, "
                + FrameEntry.COLUMN_FRAME_NUMBER + " INTEGER NOT NULL, "
                + FrameEntry.COLUMN_IS_ACCESSED + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[0] + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[1] + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_PIN_STATE[2] + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_FOULS + " INTEGER NOT NULL DEFAULT 0, "
                + FrameEntry.COLUMN_GAME_ID + " INTEGER NOT NULL"
                + " REFERENCES " + GameEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + FrameEntry.COLUMN_FRAME_NUMBER + " >= 1 AND "
                + FrameEntry.COLUMN_FRAME_NUMBER + " <= 10), "
                + "CHECK (" + FrameEntry.COLUMN_IS_ACCESSED + " = 0 OR "
                + FrameEntry.COLUMN_IS_ACCESSED + " = 1)"
                + ");");
        db.execSQL("INSERT INTO frame2 ("
                + FrameEntry._ID + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2] + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_GAME_ID + ")"
                + " SELECT "
                + FrameEntry._ID + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2] + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_GAME_ID + " FROM " + FrameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE " + FrameEntry.TABLE_NAME);
        db.execSQL("ALTER TABLE frame2 RENAME TO " + FrameEntry.TABLE_NAME);

        db.execSQL("CREATE INDEX frame_id_index ON "
                + FrameEntry.TABLE_NAME + "(" + FrameEntry._ID + ")");
        db.execSQL("CREATE INDEX frame_game_fk_index ON "
                + FrameEntry.TABLE_NAME + "(" + FrameEntry.COLUMN_GAME_ID + ")");

        try {
            db.beginTransaction();
            for (int i = 0; i < 32; i++) {
                for (int j = 0; j < 3; j++) {
                    ContentValues values = new ContentValues();
                    values.put(FrameEntry.COLUMN_PIN_STATE[j], i);
                    db.update(FrameEntry.TABLE_NAME,
                            values,
                            FrameEntry.COLUMN_PIN_STATE[j] + "=?",
                            new String[]{
                                    String.format("%5s",
                                            Integer.toBinaryString(i)).replace(' ', '0')
                            });
                }
            }
            for (int i = 24; i < 31; i++) {
                ContentValues values = new ContentValues();
                values.put(FrameEntry.COLUMN_FOULS, i);
                db.update(FrameEntry.TABLE_NAME,
                        values,
                        FrameEntry.COLUMN_FOULS + "=?",
                        new String[]{Score.foulIntToString(i)});
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Error upgrading db from 2 to 3", ex);
            dropTablesAndRecreate(db);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Upgrades database from oldVersion 3 to newVersion 4.
     *
     * @param db to upgrade
     */
    private void upgradeDatabaseFrom3To4(SQLiteDatabase db) {
        createMatchPlayTable(db);
        db.execSQL("CREATE INDEX match_game_fk_index ON "
                + MatchPlayEntry.TABLE_NAME + "(" + MatchPlayEntry.COLUMN_GAME_ID + ")");
        db.execSQL("CREATE INDEX match_id_index ON "
                + MatchPlayEntry.TABLE_NAME + "(" + MatchPlayEntry._ID + ")");
    }
}

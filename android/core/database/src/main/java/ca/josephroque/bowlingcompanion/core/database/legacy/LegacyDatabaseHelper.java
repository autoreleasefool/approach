package ca.josephroque.bowlingcompanion.core.database.legacy;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Locale;

import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.BowlerEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.FrameEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.GameEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.LeagueEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.MatchPlayEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.SeriesEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.TeamBowlerEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyContract.TeamEntry;
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyFouls;
import ca.josephroque.bowlingcompanion.core.database.legacy.model.LegacyLeague;

/**
 * Copyright (C) 2015 Joseph Roque
 * Manages interactions with the application's database, including the creation,
 * updates and deletion.
 * @noinspection deprecation
 */
public final class LegacyDatabaseHelper extends SQLiteOpenHelper {

    /** Identifies output from this class in Logcat. */
    private static final String TAG = "DBHelper";

    /** Name of the database. */
    public static final String DATABASE_NAME = "bowlingdata";

    /** Version of the database, incremented with changes. */
    private static final int DATABASE_VERSION = 7;

    /** Singleton instance of the DatabaseHelper. */
    private static LegacyDatabaseHelper sDatabaseHelperInstance = null;

    /**
     * Returns a singleton instance of DatabaseHelper.
     *
     * @param context the current activity
     * @return static instance of DatabaseHelper
     */
    public static LegacyDatabaseHelper getInstance(Context context, String name) {
        if (sDatabaseHelperInstance == null) {
            sDatabaseHelperInstance = new LegacyDatabaseHelper(context, name);
        }
        return sDatabaseHelperInstance;
    }

    /**
     * Close the current instance of the database helper.
     */
    public static void closeInstance() {
        if (sDatabaseHelperInstance != null) {
            sDatabaseHelperInstance.close();
            sDatabaseHelperInstance = null;
        }
    }

    /**
     * Private constructor for singleton access.
     *
     * @param context the current activity
     */
    private LegacyDatabaseHelper(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
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
        createTeamTable(db);
        createTeamBowlerTable(db);
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
        db.execSQL("CREATE INDEX team_id_index ON "
                + TeamEntry.TABLE_NAME + "(" + TeamEntry._ID + ")");

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
        db.execSQL("CREATE INDEX team_bowler_bowler_fk_index ON "
                + TeamBowlerEntry.TABLE_NAME + "(" + TeamBowlerEntry.COLUMN_BOWLER_ID + ")");
        db.execSQL("CREATE INDEX team_bowler_team_fk_index ON "
                + TeamBowlerEntry.TABLE_NAME + "(" + TeamBowlerEntry.COLUMN_TEAM_ID + ")");
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
        // To keep databases consistent, ignore deprecated database fields
        // noinspection deprecation
        db.execSQL("CREATE TABLE "
                + LeagueEntry.TABLE_NAME + "("
                + LeagueEntry._ID + " INTEGER PRIMARY KEY, "
                + LeagueEntry.COLUMN_LEAGUE_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " INTEGER NOT NULL, "
                + LeagueEntry.COLUMN_BASE_AVERAGE + " INTEGER NOT NULL DEFAULT -1, "
                + LeagueEntry.COLUMN_BASE_GAMES + " INTEGER NOT NULL DEFAULT 0, "
                + LeagueEntry.COLUMN_ADDITIONAL_PINFALL + " INTEGER NOT NULL DEFAULT 0, "
                + LeagueEntry.COLUMN_ADDITIONAL_GAMES + " INTEGER NOT NULL DEFAULT 0, "
                + LeagueEntry.COLUMN_GAME_HIGHLIGHT + " INTEGER NOT NULL DEFAULT -1, "
                + LeagueEntry.COLUMN_SERIES_HIGHLIGHT + " INTEGER NOT NULL DEFAULT -1, "
                + LeagueEntry.COLUMN_DATE_MODIFIED + " TEXT NOT NULL, "
                + LeagueEntry.COLUMN_IS_EVENT + " INTEGER NOT NULL DEFAULT 0, "
                + LeagueEntry.COLUMN_BOWLER_ID + " INTEGER NOT NULL"
                + " REFERENCES " + BowlerEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "CHECK (" + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " > 0 AND "
                + LeagueEntry.COLUMN_NUMBER_OF_GAMES + " <= 20), "
                + "CHECK (" + LeagueEntry.COLUMN_IS_EVENT + " = 0 OR "
                + LeagueEntry.COLUMN_IS_EVENT + " = 1), "
                + "CHECK (" + LeagueEntry.COLUMN_BASE_AVERAGE + " >= -1 AND "
                + LeagueEntry.COLUMN_BASE_AVERAGE + " <= 450), "
                + "CHECK (" + LeagueEntry.COLUMN_BASE_GAMES + " >= 0 AND "
                + LeagueEntry.COLUMN_BASE_GAMES + "<= 100000)"
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
     * Executes SQL statement to create table to store teams.
     *
     * @param db database
     */
    private void createTeamTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TeamEntry.TABLE_NAME + "("
                + TeamEntry._ID + " INTEGER PRIMARY KEY, "
                + TeamEntry.COLUMN_TEAM_NAME + " TEXT NOT NULL COLLATE NOCASE, "
                + TeamEntry.COLUMN_DATE_MODIFIED + " TEXT NOT NULL"
                + ");");
    }

    /**
     * Executes SQL statement to create table to store team members.
     *
     * @param db database
     */
    private void createTeamBowlerTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TeamBowlerEntry.TABLE_NAME + "("
                + TeamBowlerEntry.COLUMN_BOWLER_ID + " INTEGER NOT NULL"
                + " REFERENCES " + BowlerEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + TeamBowlerEntry.COLUMN_TEAM_ID + " INTEGER NOT NULL"
                + " REFERENCES " + TeamEntry.TABLE_NAME
                + " ON UPDATE CASCADE ON DELETE CASCADE, "
                + "PRIMARY KEY (" + TeamBowlerEntry.COLUMN_BOWLER_ID + ", " + TeamBowlerEntry.COLUMN_TEAM_ID + ")"
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

    @SuppressWarnings("CheckStyle")
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
                case 5:
                    upgradeDatabaseFrom4To5(db);
                    break;
                case 6:
                    upgradeDatabaseFrom5to6(db);
                    break;
                case 7:
                    upgradeDatabaseFrom6to7(db);
                    break;
            }
            upgradeTo++;
        }
    }

    /**
     * Upgrades database from oldVersion 1 to newVersion 2.
     *
     * @param db to upgrade
     */
    @SuppressWarnings("CheckStyle")
    private void upgradeDatabaseFrom1To2(SQLiteDatabase db) {
        // Removes foreign key and check constraints from frame table
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

        // Adds new column and check constraints to game table
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

        // Adds foreign key and check constraints to frame table
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
    @SuppressWarnings("CheckStyle")
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
                                    String.format(Locale.CANADA, "%5s", Integer.toBinaryString(i)).replace(' ', '0')
                            });
                }
            }
            for (int i = 24; i < 31; i++) {
                ContentValues values = new ContentValues();
                values.put(FrameEntry.COLUMN_FOULS, i);
                db.update(FrameEntry.TABLE_NAME,
                        values,
                        FrameEntry.COLUMN_FOULS + "=?",
                        new String[]{LegacyFouls.INSTANCE.foulIntToString(i)});
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Error upgrading db from 2 to 3", ex);
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

    /**
     * Upgrades database from oldVersion 4 to newVersion 5.
     *
     * @param db to upgrade
     */
    private void upgradeDatabaseFrom4To5(SQLiteDatabase db) {
        // To keep databases consistent, ignore deprecated database fields
        // noinspection deprecation
        db.execSQL("ALTER TABLE " + LeagueEntry.TABLE_NAME
                + " ADD COLUMN "
                + LeagueEntry.COLUMN_BASE_AVERAGE + " INTEGER NOT NULL DEFAULT -1;");
        // noinspection deprecation
        db.execSQL("ALTER TABLE " + LeagueEntry.TABLE_NAME
                + " ADD COLUMN "
                + LeagueEntry.COLUMN_BASE_GAMES + " INTEGER NOT NULL DEFAULT 0;");
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            // noinspection deprecation
            values.put(LeagueEntry.COLUMN_BASE_AVERAGE, -1);
            // noinspection deprecation
            values.put(LeagueEntry.COLUMN_BASE_GAMES, 0);
            db.update(LeagueEntry.TABLE_NAME, values, null, null);
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Error upgrading from 4 to 5", ex);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Upgrades database from oldVersion 5 to newVersion 6.
     *
     * @param db to upgrade
     */
    private void upgradeDatabaseFrom5to6(SQLiteDatabase db) {
        // To keep databases consistent, ignore deprecated database fields
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            // noinspection deprecation
            values.put(LeagueEntry.COLUMN_BASE_GAMES, 0);
            // noinspection deprecation
            db.update(LeagueEntry.TABLE_NAME,
                    values,
                    LeagueEntry.COLUMN_BASE_GAMES + "=?",
                    new String[]{String.valueOf(-1)});
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Error upgrading from 5 to 6");
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Upgrades database from oldVersion 6 to newVersion 7.
     *
     * @param db to upgrade
     */
    private void upgradeDatabaseFrom6to7(SQLiteDatabase db) {
        createTeamTable(db);
        createTeamBowlerTable(db);

        db.execSQL("ALTER TABLE " + LeagueEntry.TABLE_NAME
                + " ADD COLUMN "
                + LeagueEntry.COLUMN_ADDITIONAL_PINFALL + " INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("ALTER TABLE " + LeagueEntry.TABLE_NAME
                + " ADD COLUMN "
                + LeagueEntry.COLUMN_ADDITIONAL_GAMES + " INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("ALTER TABLE " + LeagueEntry.TABLE_NAME
                + " ADD COLUMN "
                + LeagueEntry.COLUMN_GAME_HIGHLIGHT + " INTEGER NOT NULL DEFAULT -1;");
        db.execSQL("ALTER TABLE " + LeagueEntry.TABLE_NAME
                + " ADD COLUMN "
                + LeagueEntry.COLUMN_SERIES_HIGHLIGHT + " INTEGER NOT NULL DEFAULT -1;");

        try {
            db.beginTransaction();

            // Replace existing `Practice` leagues from user with `Practice league`
            ContentValues values = new ContentValues();
            values.put(LeagueEntry.COLUMN_LEAGUE_NAME, LegacyLeague.PRACTICE_LEAGUE_NAME + " league");
            db.update(
                    LeagueEntry.TABLE_NAME,
                    values,
                    LeagueEntry.COLUMN_LEAGUE_NAME + "=? AND " + LeagueEntry.COLUMN_IS_EVENT + "=?",
                    new String[]{LegacyLeague.PRACTICE_LEAGUE_NAME, "0"}
            );

            // Replace existing `Practice` events from user with `Practice event`
            values = new ContentValues();
            values.put(LeagueEntry.COLUMN_LEAGUE_NAME, LegacyLeague.PRACTICE_LEAGUE_NAME + " event");
            db.update(
                    LeagueEntry.TABLE_NAME,
                    values,
                    LeagueEntry.COLUMN_LEAGUE_NAME + "=? AND " + LeagueEntry.COLUMN_IS_EVENT + "=?",
                    new String[]{LegacyLeague.PRACTICE_LEAGUE_NAME, "1"}
            );

            // Replace `Open` league with `Practice` league
            values = new ContentValues();
            values.put(LeagueEntry.COLUMN_LEAGUE_NAME, LegacyLeague.PRACTICE_LEAGUE_NAME);
            db.update(LeagueEntry.TABLE_NAME,
                    values,
                    LeagueEntry.COLUMN_LEAGUE_NAME + "=?",
                    new String[]{LegacyLeague.OPEN_LEAGUE_NAME});
            // noinspection deprecation
            db.execSQL("UPDATE " + LeagueEntry.TABLE_NAME + " SET "
                    + LeagueEntry.COLUMN_ADDITIONAL_GAMES + "=" + LeagueEntry.COLUMN_BASE_GAMES + ", "
                    + LeagueEntry.COLUMN_ADDITIONAL_PINFALL + "=" + LeagueEntry.COLUMN_BASE_GAMES + "*" + LeagueEntry.COLUMN_BASE_AVERAGE);

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Error upgrading from 6 to 7", ex);
        } finally {
            db.endTransaction();
        }
    }
}
package ca.josephroque.bowlingcompanion.database;

import android.provider.BaseColumns;

/**
 * Created by josephroque on 15-01-12.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.database
 * in project Bowling Companion
 */
public class BowlingContract
{

    /**
     * Private constructor, cannot be instantiated
     */
    private BowlingContract(){}

    /**
     * Table and column names for SQLite table relevant to bowlers
     */
    public static final class BowlerEntry implements BaseColumns
    {
        /**
         * Private constructor, cannot be instantiated
         */
        private BowlerEntry(){}

        /** Name of the table for bowler data */
        public static final String TABLE_NAME = "bowlers";
        /** Name of the column for bowler names */
        public static final String COLUMN_NAME_BOWLER_NAME = "bowler_name";
        /** Name of the column for bowlers' most recent date modified */
        public static final String COLUMN_NAME_DATE_MODIFIED = "bowler_date_modified";
    }

    /**
     * Table and column names for SQLite table relevant to leagues
     */
    public static final class LeagueEntry implements BaseColumns
    {
        /**
         * Private constructor, cannot be instantiated
         */
        private LeagueEntry(){}

        /** Name of the table for league data */
        public static final String TABLE_NAME = "leagues";
        /** Name of the column for league names */
        public static final String COLUMN_NAME_LEAGUE_NAME = "league_name";
        /** Name of the column for leagues' number of games */
        public static final String COLUMN_NAME_NUMBER_OF_GAMES = "league_number_of_games";
        /** Name of the column for leagues' most recent date modified */
        public static final String COLUMN_NAME_DATE_MODIFIED = "league_date_modified";
        /** Name of the column for indicating if league is a tournament or not */
        public static final String COLUMN_NAME_IS_TOURNAMENT = "league_is_tournament";
        /** Name of the column for leagues' foreign key to bowler IDs */
        public static final String COLUMN_NAME_BOWLER_ID = "league_bowler_id_fk";
    }

    /**
     * Table and column names for SQLite table relevant to series
     */
    public static final class SeriesEntry implements BaseColumns
    {
        /**
         * Private constructor, cannot be instantiated
         */
        private SeriesEntry(){}

        /** Name of the table for series data */
        public static final String TABLE_NAME = "series";
        /** Name of the column for series' date created */
        public static final String COLUMN_NAME_DATE_CREATED = "series_date_created";
        /** Name of the column for series' foreign key to league IDs */
        public static final String COLUMN_NAME_LEAGUE_ID = "series_league_id_fk";
        /** Name of the column for series' foreign key to bowler IDs */
        public static final String COLUMN_NAME_BOWLER_ID = "series_bowler_id_fk";
    }

    /**
     * Table and column names for SQLite table relevant to games
     */
    public static final class GameEntry implements BaseColumns
    {
        /**
         * Private constructor, cannot be instantiated
         */
        private GameEntry() {}

        /** Name of the table for game data */
        public static final String TABLE_NAME = "games";
        /** Name of the column for games' number in a series */
        public static final String COLUMN_NAME_GAME_NUMBER = "game_number";
        /** Name of the column for the final score of the game */
        public static final String COLUMN_NAME_GAME_FINAL_SCORE = "game_final_score";
        /** Name of the column for games' foreign key to league IDs */
        public static final String COLUMN_NAME_LEAGUE_ID = "game_league_id_fk";
        /** Name of the column for games' foreign key to series IDs */
        public static final String COLUMN_NAME_SERIES_ID = "game_series_id_fk";
        /** Name of the column for games' foreign key to bowler IDs */
        public static final String COLUMN_NAME_BOWLER_ID = "game_bowler_id_fk";
    }

    /**
     * Table and column names for SQLite table relevant to frames
     */
    public static final class FrameEntry implements BaseColumns
    {
        /**
         * Private constructor, cannot be instantiated
         */
        private FrameEntry(){}

        /** Name of the table for frame data */
        public static final String TABLE_NAME = "frames";
        /** Name of the column for frames' number in a game */
        public static final String COLUMN_NAME_FRAME_NUMBER = "frame_number";
        /** Name of the column for whether the frame has been accessed or not */
        public static final String COLUMN_NAME_FRAME_ACCESSED = "frame_accessed";
        /** Names of the columns for frames' balls */
        public static final String COLUMN_NAME_BALL[] = {
                "frame_ball_1", "frame_ball_2", "frame_ball_3"};
        /** Name of the columns for the frames' fouls */
        public static final String COLUMN_NAME_FOULS = "frame_fouls";
        /** Name of the column for frames' foreign key to game IDs */
        public static final String COLUMN_NAME_GAME_ID = "frame_game_id_fk";
        /** Name of the column for frames' foreign key to league IDs */
        public static final String COLUMN_NAME_LEAGUE_ID = "frame_league_id_fk";
        /** Name of the column for frames' foreign key to bowler IDs */
        public static final String COLUMN_NAME_BOWLER_ID = "frame_bowler_id_fk";
    }
}

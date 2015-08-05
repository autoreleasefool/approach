package ca.josephroque.bowlingcompanion.database;

import android.provider.BaseColumns;

/**
 * Created by Joseph Roque on 15-03-12. Defines various objects which represent tables in the schema
 * for the application's database. Member variables represent identifiers for attributes of the
 * tables.
 */
public final class Contract
{

    /**
     * Private constructor, class cannot be instantiated.
     */
    private Contract()
    {
        // does nothing
    }

    /**
     * Table and column names for SQLite table relevant to bowlers.
     */
    public static final class BowlerEntry
            implements BaseColumns
    {

        /**
         * Private constructor, class cannot be instantiated.
         */
        private BowlerEntry()
        {
            // does nothing
        }

        /** Name of the table for bowler data. */
        public static final String TABLE_NAME = "bowlers";
        /** Name of the column for bowler names. */
        public static final String COLUMN_BOWLER_NAME = "bowler_name";
        /** Name of the column for bowlers' most recent date modified. */
        public static final String COLUMN_DATE_MODIFIED = "bowler_date_modified";
    }

    /**
     * Table and column names for SQLite table relevant to leagues.
     */
    public static final class LeagueEntry
            implements BaseColumns
    {

        /**
         * Private constructor, class cannot be instantiated.
         */
        private LeagueEntry()
        {
            // does nothing
        }

        /** Name of the table for league data. */
        public static final String TABLE_NAME = "leagues";
        /** Name of the column for league names. */
        public static final String COLUMN_LEAGUE_NAME = "league_name";
        /** Name of the column for the number of games in a league. */
        public static final String COLUMN_NUMBER_OF_GAMES = "league_number_of_games";
        /** Name of the column for the leagues' most recent date modified. */
        public static final String COLUMN_DATE_MODIFIED = "league_date_modified";
        /** Name of the column to indicate if the row is an event. */
        public static final String COLUMN_IS_EVENT = "league_is_event";
        /** Name of the column for foreign key to a bowler id. */
        public static final String COLUMN_BOWLER_ID = "league_bowler_id_fk";
    }

    /**
     * Table and column names for SQLite table relevant to series.
     */
    public static final class SeriesEntry
            implements BaseColumns
    {

        /**
         * Private constructor, class cannot be instantiated.
         */
        private SeriesEntry()
        {
            // does nothing
        }

        /** Name of the table for series data. */
        public static final String TABLE_NAME = "series";
        /** Name of the table for the date of the series. */
        public static final String COLUMN_SERIES_DATE = "series_date";
        /** Name of the table for foreign key to a league id. */
        public static final String COLUMN_LEAGUE_ID = "series_league_id_fk";
    }

    /**
     * Table and column names for SQLite table relevant to games.
     */
    public static final class GameEntry
            implements BaseColumns
    {

        /**
         * Private constructor, class cannot be instantiated.
         */
        private GameEntry()
        {
            // does nothing
        }

        /** Name of the table for game data. */
        public static final String TABLE_NAME = "games";
        /** Name of the column for game number in a series. */
        public static final String COLUMN_GAME_NUMBER = "game_number";
        /** Name of the column for the game's score. */
        public static final String COLUMN_SCORE = "game_score";
        /** Name of the column to indicate if a game is locked from editing. */
        public static final String COLUMN_IS_LOCKED = "game_is_locked";
        /** Name of the column to indicate if a game's score was manually set. */
        public static final String COLUMN_IS_MANUAL = "game_is_manual";
        /** Name of the column to indicate the match play result. */
        public static final String COLUMN_MATCH_PLAY = "game_match_play";
        /** Name of the column for foreign key to a series id. */
        public static final String COLUMN_SERIES_ID = "game_series_id_fk";
    }

    /**
     * Table and column names for SQLite table relevant to frames.
     */
    public static final class FrameEntry
            implements BaseColumns
    {

        /**
         * Private constructor, class cannot be instantiated.
         */
        private FrameEntry()
        {
            // does nothing
        }

        /** Name of the table for frame data. */
        public static final String TABLE_NAME = "frames";
        /** Name of the column for the frame number in a game. */
        public static final String COLUMN_FRAME_NUMBER = "frame_number";
        /** Name of the column to indicate if a frame has been accessed. */
        public static final String COLUMN_IS_ACCESSED = "frame_accessed";
        /** Name of the column to indicate the state of the pins in a frame. */
        public static final String[] COLUMN_PIN_STATE =
                {"frame_pins_1", "frame_pins_2", "frame_pins_3"};
        /** Name of the column to indicate if fouls were invoked in a frame. */
        public static final String COLUMN_FOULS = "frame_fouls";
        /** Name of the column for foreign key to a game id. */
        public static final String COLUMN_GAME_ID = "frame_game_id_fk";
    }
}

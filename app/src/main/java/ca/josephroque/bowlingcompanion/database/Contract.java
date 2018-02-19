package ca.josephroque.bowlingcompanion.database;

import android.provider.BaseColumns;

/**
 * Copyright (c) 2015 Joseph Roque
 *
 * Defines various objects which represent tables in the schema for the
 * application's database. Member variables represent identifiers for attributes of the tables.
 */
public final class Contract {

    /**
     * Table and column names for SQLite table relevant to bowlers.
     */
    public static final class BowlerEntry
            implements BaseColumns {

        /** Name of the table for bowler data. */
        public static final String TABLE_NAME = "bowlers";
        /** Name of the column for bowler names. */
        public static final String COLUMN_BOWLER_NAME = "bowler_name";
        /** Name of the column for bowlers' most recent date modified. */
        public static final String COLUMN_DATE_MODIFIED = "bowler_date_modified";

        /**
         * Private constructor, class cannot be instantiated.
         */
        private BowlerEntry() {
            // does nothing
        }
    }

    /**
     * Table and column names for SQLite table relevant to leagues.
     */
    public static final class LeagueEntry
            implements BaseColumns {

        /** Name of the table for league data. */
        public static final String TABLE_NAME = "leagues";
        /** Name of the column for league names. */
        public static final String COLUMN_LEAGUE_NAME = "league_name";
        /** Name of the column for the number of games in a league. */
        public static final String COLUMN_NUMBER_OF_GAMES = "league_number_of_games";
        /** Name of the column for the additional pinfall of the league. */
        public static final String COLUMN_BASE_AVERAGE = "league_base_avg";
        /** Name of the column for the base number of games of the league. */
        public static final String COLUMN_BASE_GAMES = "league_base_games";
        /** Name of the column for the additional pinfall of the league. */
        public static final String COLUMN_ADDITIONAL_PINFALL = "league_additional_pinfall";
        /** Name of the column for the additional games of the league. */
        public static final String COLUMN_ADDITIONAL_GAMES = "league_additional_games";
        /** Name of the column for the leagues' most recent date modified. */
        public static final String COLUMN_DATE_MODIFIED = "league_date_modified";
        /** Name of the column to indicate if the row is an event. */
        public static final String COLUMN_IS_EVENT = "league_is_event";
        /** Name of the column for foreign key to a bowler id. */
        public static final String COLUMN_BOWLER_ID = "league_bowler_id_fk";

        /**
         * Private constructor, class cannot be instantiated.
         */
        private LeagueEntry() {
            // does nothing
        }
    }

    /**
     * Table and column names for SQLite table relevant to series.
     */
    public static final class SeriesEntry
            implements BaseColumns {

        /** Name of the table for series data. */
        public static final String TABLE_NAME = "series";
        /** Name of the table for the date of the series. */
        public static final String COLUMN_SERIES_DATE = "series_date";
        /** Name of the table for foreign key to a league id. */
        public static final String COLUMN_LEAGUE_ID = "series_league_id_fk";

        /**
         * Private constructor, class cannot be instantiated.
         */
        private SeriesEntry() {
            // does nothing
        }
    }

    /**
     * Table and column names for SQLite table relevant to games.
     */
    public static final class GameEntry
            implements BaseColumns {

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

        /**
         * Private constructor, class cannot be instantiated.
         */
        private GameEntry() {
            // does nothing
        }
    }

    /**
     * Table and column names for SQLite table relevant to frames.
     */
    public static final class FrameEntry
            implements BaseColumns {

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

        /**
         * Private constructor, class cannot be instantiated.
         */
        private FrameEntry() {
            // does nothing
        }
    }

    /**
     * Table and column names for SQLite table relevant to match play results.
     */
    public static final class MatchPlayEntry
            implements BaseColumns {

        /** Name of the table for match play data. */
        public static final String TABLE_NAME = "match_play";
        /** Name of the column for the opponent's name. */
        public static final String COLUMN_OPPONENT_NAME = "match_opp_name";
        /** Name of the column for the opponent's score. */
        public static final String COLUMN_OPPONENT_SCORE = "match_opp_score";
        /** Name of the column for foreign key to a game id. */
        public static final String COLUMN_GAME_ID = "match_game_id_fk";

        /**
         * Private constructor, class cannot be instantiated.
         */
        private MatchPlayEntry() {
            // does nothing
        }
    }

    public static final class TeamEntry
            implements BaseColumns {

        /** Name of the table for team data. */
        public static final String TABLE_NAME = "teams";
        /** Name of the column for the team name. */
        public static final String COLUMN_TEAM_NAME = "team_name";
        /** Name of the column for teams' most recent date modified. */
        public static final String COLUMN_DATE_MODIFIED = "team_date_modified";

        /**
         * Private constructor, class cannot be instantiated.
         */
        private TeamEntry() {
            // does nothing
        }
    }

    public static final class TeamBowlerEntry
            implements BaseColumns {

        /** Name of the table for team bowlers data. */
        public static final String TABLE_NAME = "teams_bowlers";
        /** Name of the column for the team id. */
        public static final String COLUMN_TEAM_ID = "team_id_fk";
        /** Name of the column for the bowler id. */
        public static final String COLUMN_BOWLER_ID = "bowler_id_fk";

        /**
         * Private constructor, class cannot be instantiated.
         */
        private TeamBowlerEntry() {
            // does nothing
        }
    }

    /**
     * Private constructor, class cannot be instantiated.
     */
    private Contract() {
        // does nothing
    }
}

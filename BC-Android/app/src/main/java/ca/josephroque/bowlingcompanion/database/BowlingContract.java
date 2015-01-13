package ca.josephroque.bowlingcompanion.database;

import android.provider.BaseColumns;

/**
 * Created by josephroque on 15-01-12.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class BowlingContract
{

    private BowlingContract(){}

    public static final class BowlerEntry implements BaseColumns
    {
        private BowlerEntry(){}

        public static final String TABLE_NAME = "bowlers";
        public static final String COLUMN_NAME_BOWLER_NAME = "bowler_name";
        public static final String COLUMN_NAME_DATE_MODIFIED = "bowler_date_modified";
    }

    public static final class LeagueEntry implements BaseColumns
    {
        private LeagueEntry(){}

        public static final String TABLE_NAME = "leagues";
        public static final String COLUMN_NAME_LEAGUE_NAME = "league_name";
        public static final String COLUMN_NAME_NUMBER_OF_GAMES = "league_number_of_games";
        public static final String COLUMN_NAME_DATE_MODIFIED = "league_date_modified";
        public static final String COLUMN_NAME_BOWLER_ID = "league_bowler_id_fk";
    }

    public static final class SeriesEntry implements BaseColumns
    {
        private SeriesEntry(){}

        public static final String TABLE_NAME = "series";
        public static final String COLUMN_NAME_DATE_CREATED = "series_date_created";
        public static final String COLUMN_NAME_LEAGUE_ID = "series_league_id_fk";
        public static final String COLUMN_NAME_BOWLER_ID = "series_bowler_id_fk";
    }

    public static final class GameEntry implements BaseColumns
    {
        private GameEntry() {}

        public static final String TABLE_NAME = "games";
        public static final String COLUMN_NAME_GAME_NUMBER = "game_number";
        public static final String COLUMN_NAME_GAME_FINAL_SCORE = "game_final_score";
        public static final String COLUMN_NAME_SERIES_ID = "game_series_id_fk";
        public static final String COLUMN_NAME_BOWLER_ID = "game_bowler_id_fk";
    }

    public static final class FrameEntry implements BaseColumns
    {
        private FrameEntry(){}

        public static final String TABLE_NAME = "frames";
        public static final String COLUMN_NAME_FRAME_NUMBER = "frame_number";
        public static final String COLUMN_NAME_BALL[] = {
                "frame_ball_1", "frame_ball_2", "frame_ball_3"};
        public static final String COLUMN_NAME_GAME_ID = "frame_game_id_fk";
        public static final String COLUMN_NAME_SERIES_ID = "frame_series_id_fk";
        public static final String COLUMN_NAME_LEAGUE_ID = "frame_league_id_fk";
        public static final String COLUMN_NAME_BOWLER_ID = "frame_bowler_id_fk";
    }
}

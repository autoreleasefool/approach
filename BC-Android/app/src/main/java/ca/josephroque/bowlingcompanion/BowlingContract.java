package ca.josephroque.bowlingcompanion;

import android.provider.BaseColumns;

/**
 * Created by josephroque on 15-01-09.
 */
public class BowlingContract
{

    private BowlingContract(){}

    public static final class BowlerEntry implements BaseColumns
    {
        private BowlerEntry(){}

        public static final String TABLE_NAME = "bowlers";
        public static final String COLUMN_NAME_BOWLER_NAME = "bowler_name";
    }

    public static final class LeagueEntry implements BaseColumns
    {
        private LeagueEntry(){}

        public static final String TABLE_NAME = "leagues";
        public static final String COLUMN_NAME_LEAGUE_NAME = "league_name";
        public static final String COLUMN_NAME_NUMBER_OF_GAMES = "number_of_games";
        public static final String COLUMN_NAME_BOWLER_ID = "bowler_id_fk";
    }

    public static final class SeriesEntry implements BaseColumns
    {
        private SeriesEntry(){}

        public static final String TABLE_NAME = "series";
        public static final String COLUMN_NAME_DATE_CREATED = "date_created";
        public static final String COLUMN_NAME_LEAGUE_ID = "league_id_fk";
    }

    public static final class FrameEntry implements BaseColumns
    {
        private FrameEntry(){}

        public static final String TABLE_NAME = "frames";
        public static final String COLUMN_NAME_BALL[] = {
                "ball_1", "ball_2", "ball_3"};
        public static final String COLUMN_NAME_GAME_NUMBER = "game_number";
        public static final String COLUMN_NAME_SERIES_ID = "series_id_fk";
        public static final String COLUMN_NAME_LEAGUE_ID = "league_id_fk";
        public static final String COLUMN_NAME_BOWLER_ID = "bowler_id_fk";
    }
}

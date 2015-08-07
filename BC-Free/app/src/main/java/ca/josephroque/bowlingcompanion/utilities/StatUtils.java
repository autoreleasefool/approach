package ca.josephroque.bowlingcompanion.utilities;

/**
 * Created by Joseph Roque on 2015-07-25. Methods and constants for identifying and recording
 * statistics.
 */
public final class StatUtils
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "StatUtils";

    /** Indicates all the stats related to the specified bowler should be loaded. */
    public static final byte LOADING_BOWLER_STATS = 0;
    /** Indicates all the stats related to the specified league should be loaded. */
    public static final byte LOADING_LEAGUE_STATS = 1;
    /** Indicates all the stats related to the specified series should be loaded. */
    public static final byte LOADING_SERIES_STATS = 2;
    /** Indicates only the stats related to the specified game should be loaded. */
    public static final byte LOADING_GAME_STATS = 3;

    /** Indicates index for stat category. */
    public static final byte STAT_CATEGORY_GENERAL = 0;
    /** Indicates index for stat in array. */
    public static final byte STAT_MIDDLE_HIT = 0;
    /** Indicates index for stat in array. */
    public static final byte STAT_STRIKES = 1;
    /** Indicates index for stat in array. */
    public static final byte STAT_SPARE_CONVERSIONS = 2;

    /** Indicates index for stat category. */
    public static final byte STAT_CATEGORY_FIRST_BALL = 1;
    /** Indicates index for stat in array. */
    public static final byte STAT_HEAD_PINS = 0;
    /* Indicates index for stat in array. */
    //public static final byte STAT_HEAD_PINS_SPARED = 1;
    /** Indicates index for stat in array. */
    public static final byte STAT_LEFT = 2;
    /* Indicates index for stat in array. */
    //public static final byte STAT_LEFT_SPARED = 3;
    /** Indicates index for stat in array. */
    public static final byte STAT_RIGHT = 4;
    /* Indicates index for stat in array. */
    //public static final byte STAT_RIGHT_SPARED = 5;
    /** Indicates index for stat in array. */
    public static final byte STAT_ACES = 6;
    /* Indicates index for stat in array. */
    //public static final byte STAT_ACES_SPARED = 7;
    /** Indicates index for stat in array. */
    public static final byte STAT_CHOP = 8;
    /* Indicates index for stat in array. */
    //public static final byte STAT_CHOP_SPARED = 9;
    /** Indicates index for stat in array. */
    public static final byte STAT_LEFT_CHOP = 10;
    /* Indicates index for stat in array. */
    //public static final byte STAT_LEFT_CHOP_SPARED = 11;
    /** Indicates index for stat in array. */
    public static final byte STAT_RIGHT_CHOP = 12;
    /* Indicates index for stat in array. */
    //public static final byte STAT_RIGHT_CHOP_SPARED = 13;
    /** Indicates index for stat in array. */
    public static final byte STAT_SPLIT = 14;
    /* Indicates index for stat in array. */
    //public static final byte STAT_SPLIT_SPARED = 15;
    /** Indicates index for stat in array. */
    public static final byte STAT_LEFT_SPLIT = 16;
    /* Indicates index for stat in array. */
    //public static final byte STAT_LEFT_SPLIT_SPARED = 17;
    /** Indicates index for stat in array. */
    public static final byte STAT_RIGHT_SPLIT = 18;
    /** Indicates index for stat in array. */
    public static final byte STAT_RIGHT_SPLIT_SPARED = 19;

    /** Indicates index for stat category. */
    public static final byte STAT_CATEGORY_FOULS = 2;
    /** Indicates index for stat in array. */
    public static final byte STAT_FOULS = 0;

    /** Indicates index for stat category. */
    public static final byte STAT_CATEGORY_PINS = 3;
    /** Indicates index for stat in array. */
    public static final byte STAT_PINS_LEFT = 0;
    /** Indicates index for stat in array. */
    public static final byte STAT_PINS_AVERAGE = 1;

    /** Indicates index for stat category. */
    public static final byte STAT_CATEGORY_AVERAGE_BY_GAME = 4;

    /** Indicates index for stat category. */
    public static final byte STAT_CATEGORY_MATCH_PLAY = 5;
    /** Indicates index for stat in array. */
    public static final byte STAT_WON = 0;
    /** Indicates index for stat in array. */
    public static final byte STAT_LOST = 1;
    /** Indicates index for stat in array. */
    public static final byte STAT_TIED = 2;

    /** Indicates index for stat category. */
    public static final byte STAT_CATEGORY_OVERALL = 6;
    /** Indicates index for stat in array. */
    public static final byte STAT_AVERAGE = 0;
    /** Indicates index for stat in array. */
    public static final byte STAT_HIGH_SINGLE = 1;
    /** Indicates index for stat in array. */
    public static final byte STAT_HIGH_SERIES = 2;
    /** Indicates index for stat in array. */
    public static final byte STAT_TOTAL_PINS = 3;
    /** Indicates index for stat in array. */
    public static final byte STAT_NUMBER_OF_GAMES = 4;

    /**
     * Gets the name of a stat based on its category and index in that category.
     *
     * @param statCategory category of the stat
     * @param statIndex index of the stat
     * @param chanceName if true, returns a name which describes the chances the user had to
     * increase the stat, or null if such a name does not exist. If false, returns the name of the
     * stat
     * @return name of the stat or the "chances" name of the stat
     */
    public static String getStatName(int statCategory, int statIndex, boolean chanceName)
    {
        switch (statCategory)
        {
            case StatUtils.STAT_CATEGORY_GENERAL:
                return getGeneralStatName(statIndex, chanceName);
            case StatUtils.STAT_CATEGORY_FIRST_BALL:
                return getFirstBallStatName(statIndex, chanceName);
            case StatUtils.STAT_CATEGORY_FOULS:
                return getFoulStatName(statIndex, chanceName);
            case StatUtils.STAT_CATEGORY_PINS:
                return getPinStatName(statIndex, chanceName);
            case StatUtils.STAT_CATEGORY_AVERAGE_BY_GAME:
                return getAverageByGameStatName(statIndex, chanceName);
            case StatUtils.STAT_CATEGORY_MATCH_PLAY:
                return getMatchPlayStatName(statIndex, chanceName);
            case StatUtils.STAT_CATEGORY_OVERALL:
                return getOverallStatName(statIndex, chanceName);
            default:
                throw new IllegalArgumentException("invalid stat category: " + statCategory);
        }
    }

    /**
     * Gets the name of a stat in the "overall" category.
     *
     * @param statIndex index of the stat
     * @param chanceName if true, method returns null
     * @return name of the "overall" stat
     */
    private static String getOverallStatName(int statIndex, boolean chanceName)
    {
        if (chanceName)
            return null;

        switch (statIndex)
        {
            case STAT_AVERAGE:
                return "Average";
            case STAT_HIGH_SINGLE:
                return "High Single";
            case STAT_HIGH_SERIES:
                return "High Series";
            case STAT_TOTAL_PINS:
                return "Total Pinfall";
            case STAT_NUMBER_OF_GAMES:
                return "# of Games";
            default:
                throw new IllegalArgumentException(
                        "invalid index " + statIndex + " for overall category");
        }
    }

    /**
     * Gets the name of a stat in the "match" category.
     *
     * @param statIndex index of the stat
     * @param chanceName if true, returns a stat name representing the chances a user could have
     * achieved a stat. If false, returns the name of the achievable stat
     * @return name of the "match" stat
     */
    private static String getMatchPlayStatName(int statIndex, boolean chanceName)
    {
        if (chanceName)
            return "Total Match Play Games";

        switch (statIndex)
        {
            case STAT_WON:
                return "Games Won";
            case STAT_LOST:
                return "Games Lost";
            case STAT_TIED:
                return "Games Tied";
            default:
                throw new IllegalArgumentException(
                        "invalid index " + statIndex + " for match play category");
        }
    }

    /**
     * Gets the name of a stat in the "average by game" category.
     *
     * @param statIndex index of the stat
     * @param chanceName if true, returns null
     * @return name of the "average by game" stat
     */
    private static String getAverageByGameStatName(int statIndex, boolean chanceName)
    {
        if (chanceName)
            return null;
        else
            return "Game " + (statIndex + 1);
    }

    /**
     * Gets the name of a stat in the "pins" category.
     *
     * @param statIndex index of the stat
     * @param chanceName if true, returns null
     * @return name of the "pins" stat
     */
    private static String getPinStatName(int statIndex, boolean chanceName)
    {
        if (chanceName)
            return null;

        switch (statIndex)
        {
            case STAT_PINS_LEFT:
                return "Total Pins Left";
            case STAT_PINS_AVERAGE:
                return "Average Pins Left";
            default:
                throw new IllegalArgumentException(
                        "invalid index " + statIndex + " for pins category");
        }
    }

    /**
     * Gets the name of a stat in the "foul" category.
     *
     * @param statIndex index of the stat
     * @param chanceName if true, returns null
     * @return name of the "foul" stat
     */
    private static String getFoulStatName(int statIndex, boolean chanceName)
    {
        if (chanceName)
            return null;

        switch (statIndex)
        {
            case STAT_FOULS:
                return "Fouls";
            default:
                throw new IllegalArgumentException(
                        "invalid index " + statIndex + " for fouls category");
        }
    }

    /**
     * Gets the name of a stat in the "first ball" category.
     *
     * @param statIndex index of the stat
     * @param chanceName if true, returns a stat name representing the chances a user could have
     * achieved a stat. If false, returns the name of the achievable stat
     * @return name of the "first ball" stat
     */
    private static String getFirstBallStatName(int statIndex, boolean chanceName)
    {
        boolean spared = (statIndex % 2 == 1);
        if (spared)
            statIndex--;

        final String statName;
        switch (statIndex)
        {
            case STAT_HEAD_PINS:
                statName = "Head Pins";
                break;
            case STAT_LEFT:
                statName = "Lefts";
                break;
            case STAT_RIGHT:
                statName = "Rights";
                break;
            case STAT_ACES:
                statName = "Aces";
                break;
            case STAT_CHOP:
                statName = "Chop Offs";
                break;
            case STAT_LEFT_CHOP:
                statName = "Left Chop Offs";
                break;
            case STAT_RIGHT_CHOP:
                statName = "Right Chop Offs";
                break;
            case STAT_SPLIT:
                statName = "Splits";
                break;
            case STAT_LEFT_SPLIT:
                statName = "Left Splits";
                break;
            case STAT_RIGHT_SPLIT:
                statName = "Right Splits";
                break;
            default:
                throw new IllegalArgumentException(
                        "invalid index " + statIndex + " for first ball category");
        }

        if (chanceName)
            return (spared)
                    ? "Total " + statName
                    : "Total Shots at Middle";
        else if (spared)
            return statName + " Spared";
        else
            return statName;
    }

    /**
     * Gets the name of a stat in the "general" category.
     *
     * @param statIndex index of the stat
     * @param chanceName if true, returns a stat name representing the chances a user could have
     * achieved a stat. If false, returns the name of the achievable stat
     * @return name of the "general" stat
     */
    private static String getGeneralStatName(int statIndex, boolean chanceName)
    {
        switch (statIndex)
        {
            case STAT_MIDDLE_HIT:
                return (chanceName)
                        ? "Total Shots at Middle"
                        : "Middle Hit";
            case STAT_STRIKES:
                return (chanceName)
                        ? "Total Shots at Middle"
                        : "Strikes";
            case STAT_SPARE_CONVERSIONS:
                return (chanceName)
                        ? "Total Spare Chances"
                        : "Spare Conversions";
            default:
                throw new IllegalArgumentException(
                        "invalid index " + statIndex + " for general category");
        }
    }

    /**
     * Default private constructor.
     */
    private StatUtils()
    {
        // does nothing
    }
}

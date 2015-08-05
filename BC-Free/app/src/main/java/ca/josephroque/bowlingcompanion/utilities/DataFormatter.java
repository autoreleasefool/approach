package ca.josephroque.bowlingcompanion.utilities;

/**
 * Created by Joseph Roque on 15-03-17. Provides methods to convert various formatted data into
 * different formats.
 */
@SuppressWarnings("CheckStyle")
public final class DataFormatter
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "DataFormatter";

    /**
     * Default private constructor.
     */
    private DataFormatter()
    {
        // does nothing
    }

    /**
     * Given an integer from 1-12, returns a 3-4 character string representing that month.
     *
     * @param month value representing one of 12 months
     * @return 3-4 character string representing month, or null if month is not between 1 and 12
     * (inclusive).
     */
    public static String intToMonthCompact(int month)
    {
        switch (month)
        {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return null;
        }
    }

    /**
     * Returns an index from 1-12 representing the month which was passed.
     *
     * @param strMonth string containing at least first 3 letters of a month
     * @return index from 1-12 if strMonth represents a month, -1 otherwise
     */
    public static int monthToInt(String strMonth)
    {
        if (strMonth.startsWith("Jan")) return 1;
        else if (strMonth.startsWith("Feb")) return 2;
        else if (strMonth.startsWith("Mar")) return 3;
        else if (strMonth.startsWith("Apr")) return 4;
        else if (strMonth.startsWith("May")) return 5;
        else if (strMonth.startsWith("Jun")) return 6;
        else if (strMonth.startsWith("Jul")) return 7;
        else if (strMonth.startsWith("Aug")) return 8;
        else if (strMonth.startsWith("Sep")) return 9;
        else if (strMonth.startsWith("Oct")) return 10;
        else if (strMonth.startsWith("Nov")) return 11;
        else if (strMonth.startsWith("Dec")) return 12;
        else return -1;
    }

    /**
     * Converts string of the form "YYYY-mm-DD" to a cleaner format, where YYYY stands for the year,
     * mm stands for the month and DD stands for the day.
     *
     * @param formattedDate formatted string to be converted
     * @return prettier format of string with full month name
     */
    public static String formattedDateToPrettyCompact(String formattedDate)
    {
        //Uses only first 10 characters in date
        formattedDate = formattedDate.substring(0, 10);

        if (formattedDate.matches("\\d{4}-\\d{2}-\\d{2}"))
        {
            int month = Integer.parseInt(formattedDate.substring(5, 7));
            String year = formattedDate.substring(0, 4);
            String day = formattedDate.substring(8);

            return intToMonthCompact(month) + " " + day + ", " + year;
        }
        else
            throw new IllegalArgumentException("String must be formatted as YYYY-mm-DD");
    }

    /**
     * Converts a string containing a pretty formatted date into numerical values representing
     * month, day, year (in that order).
     *
     * @param pretty string formatted by formattedDateToPrettyCompact
     * @return month, day, year of pretty (in that order)
     */
    public static int[] prettyCompactToFormattedDate(String pretty)
    {
        int[] date = new int[3];
        if (pretty.matches("[A-Z]\\w{2,3} \\d{1,2}, \\d{4}"))
        {
            date[0] = monthToInt(pretty.substring(0, 3));
            switch (date[0])
            {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 8:
                case 10:
                case 11:
                case 12:
                    date[1] = Integer.parseInt(pretty.substring(4, pretty.indexOf(",")));
                    date[2] = Integer.parseInt(pretty.substring(pretty.indexOf(",") + 2));
                    break;
                case 6:
                case 7:
                case 9:
                    date[1] = Integer.parseInt(pretty.substring(5, pretty.indexOf(",")));
                    date[2] = Integer.parseInt(pretty.substring(pretty.indexOf(",") + 2));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid month");
            }
        }
        else
            throw new IllegalArgumentException(
                    "String must match the pattern: [A-Z]\\w{2,3} \\d{1,2}, \\d{4}");

        return date;
    }

    /**
     * Converts a dp value to pixels.
     *
     * @param scale density of screen
     * @param dps value to be converted
     * @return result of conversion from dps to pixels
     */
    public static int getPixelsFromDP(float scale, int dps)
    {
        return (int) Math.ceil(dps * scale);
    }
}

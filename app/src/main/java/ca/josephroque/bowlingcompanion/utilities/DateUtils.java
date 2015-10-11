package ca.josephroque.bowlingcompanion.utilities;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Joseph Roque on 2015-07-28. Provides methods for managing dates and times as milliseconds since January 1,
 * 1970, at 00:00:00 GMT, as {@link java.util.Date} objects, or as {@link java.util.Calendar} objects.
 */
public final class DateUtils {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "DateUtils";

    /** A value in milliseconds equivalent to exactly one week. */
    public static final long MILLIS_ONE_WEEK = 1000 * 60 * 60 * 24 * 7;
    /** The length of the date in a string containing the date and time. */
    public static final int LENGTH_OF_DATE_MINUS_TIME = 10;

    /** The number of values in a date, the year, month and day. */
    private static final int DATE_COMPONENTS = 3;

    /** Numerical representation of the first month, January. */
    private static final int JAN = 1;
    /** Numerical representation of the second month, February. */
    private static final int FEB = 2;
    /** Numerical representation of the third month, March. */
    private static final int MAR = 3;
    /** Numerical representation of the fourth month, April. */
    private static final int APR = 4;
    /** Numerical representation of the fifth month, May. */
    private static final int MAY = 5;
    /** Numerical representation of the sixth month, June. */
    private static final int JUNE = 6;
    /** Numerical representation of the seventh month, July. */
    private static final int JULY = 7;
    /** Numerical representation of the eighth month, August. */
    private static final int AUG = 8;
    /** Numerical representation of the ninth month, September. */
    private static final int SEPT = 9;
    /** Numerical representation of the tenth month, October. */
    private static final int OCT = 10;
    /** Numerical representation of the eleventh month, November. */
    private static final int NOV = 11;
    /** Numerical representation of the twelfth month, December. */
    private static final int DEC = 12;

    /**
     * Formats a String of the format 'yyyy-MM-dd HH:mm:ss' to a {@link java.util.Date} object.
     */
    private static final DateFormat STRING_TO_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.CANADA);

    /**
     * Parses a String to a {@link java.util.Date}.
     *
     * @param strDate date as a string in the format 'yyyy-MM-dd HH:mm:ss'
     * @return the date represented by the value in the column
     */
    public static Date parseEntryDate(String strDate) {
        Date date;
        try {
            date = STRING_TO_DATE.parse(strDate);
        } catch (ParseException ex) {
            Log.e(TAG, "Unable to parse entry date", ex);
            return null;
        }

        return date;
    }

    /**
     * Gets a new {@link java.util.Calendar} instance set to midnight on the date of {@code date}.
     *
     * @param date date for calendar
     * @return new instance of {@code Calendar}.
     */
    public static Calendar getCalendarAtMidnight(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.CANADA);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * Given an integer from 1-12, returns a 3-4 character string representing that month.
     *
     * @param month value representing one of 12 months
     * @return 3-4 character string representing month, or null if month is not between 1 and 12 (inclusive).
     */
    public static String intToMonthCompact(int month) {
        switch (month) {
            case JAN:
                return "Jan";
            case FEB:
                return "Feb";
            case MAR:
                return "Mar";
            case APR:
                return "Apr";
            case MAY:
                return "May";
            case JUNE:
                return "June";
            case JULY:
                return "July";
            case AUG:
                return "Aug";
            case SEPT:
                return "Sept";
            case OCT:
                return "Oct";
            case NOV:
                return "Nov";
            case DEC:
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
    public static int monthToInt(String strMonth) {
        String upper = strMonth.toUpperCase();
        if (upper.startsWith("JAN")) return JAN;
        else if (upper.startsWith("FEB")) return FEB;
        else if (upper.startsWith("MAR")) return MAR;
        else if (upper.startsWith("APR")) return APR;
        else if (upper.startsWith("MAY")) return MAY;
        else if (upper.startsWith("JUN")) return JUNE;
        else if (upper.startsWith("JUL")) return JULY;
        else if (upper.startsWith("AUG")) return AUG;
        else if (upper.startsWith("SEP")) return SEPT;
        else if (upper.startsWith("OCT")) return OCT;
        else if (upper.startsWith("NOV")) return NOV;
        else if (upper.startsWith("DEC")) return DEC;
        else return -1;
    }

    /**
     * Converts string of the form "YYYY-mm-DD" to a cleaner format, where YYYY stands for the year, mm stands for the
     * month and DD stands for the day.
     *
     * @param formattedDate formatted string to be converted
     * @return prettier format of string with full month name
     */
    public static String formattedDateToPrettyCompact(String formattedDate) {
        //Uses only first 10 characters in date
        formattedDate = formattedDate.substring(0, LENGTH_OF_DATE_MINUS_TIME);

        if (formattedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            // Defines the start/end of the month in the string
            //noinspection CheckStyle
            int month = Integer.parseInt(formattedDate.substring(5, 7));

            // Defines the start/end of the year in the string
            //noinspection CheckStyle
            String year = formattedDate.substring(0, 4);

            // Defines the start/end of the day in the string
            //noinspection CheckStyle
            String day = formattedDate.substring(8);

            return intToMonthCompact(month) + " " + day + ", " + year;
        } else
            throw new IllegalArgumentException("String must be formatted as YYYY-mm-DD");
    }

    /**
     * Converts a string containing a pretty formatted date into numerical values representing month, day, year (in that
     * order).
     *
     * @param pretty string formatted by formattedDateToPrettyCompact
     * @return month, day, year of pretty (in that order)
     */
    public static int[] prettyCompactToFormattedDate(String pretty) {
        int[] date = new int[DATE_COMPONENTS];
        if (pretty.matches("[A-Z]\\w{2,3} \\d{1,2}, \\d{4}")) {
            final int monthNameLength = 3;
            date[0] = monthToInt(pretty.substring(0, monthNameLength));
            switch (date[0]) {
                case JAN:
                case FEB:
                case MAR:
                case APR:
                case MAY:
                case AUG:
                case OCT:
                case NOV:
                case DEC:
                    // Represents starting position to get date, with month names of length 3
                    //noinspection CheckStyle
                    date[1] = Integer.parseInt(pretty.substring(4, pretty.indexOf(",")));
                    date[2] = Integer.parseInt(pretty.substring(pretty.indexOf(",") + 2));
                    break;
                case JUNE:
                case JULY:
                case SEPT:
                    // Represents starting position to get date, with month names of length 4
                    //noinspection CheckStyle
                    date[1] = Integer.parseInt(pretty.substring(5, pretty.indexOf(",")));
                    date[2] = Integer.parseInt(pretty.substring(pretty.indexOf(",") + 2));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid month");
            }
        } else
            throw new IllegalArgumentException(
                    "String must match the pattern: [A-Z]\\w{2,3} \\d{1,2}, \\d{4}");

        return date;
    }

    /**
     * Default private constructor.
     */
    private DateUtils() {
        // does nothing
    }
}

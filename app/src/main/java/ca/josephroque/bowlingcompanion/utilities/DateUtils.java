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
     * Default private constructor.
     */
    private DateUtils() {
        // does nothing
    }
}

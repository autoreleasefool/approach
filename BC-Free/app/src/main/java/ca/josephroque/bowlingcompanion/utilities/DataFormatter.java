package ca.josephroque.bowlingcompanion.utilities;

/**
 * Created by josephroque on 15-03-17.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.external
 * in project Bowling Companion
 */
public class DataFormatter
{

    /**
     * Given an integer from 1-12, returns a 3-4 character
     * string representing that month
     * @param month value representing one of 12 months
     * @return 3-4 character string representing month, or null if month is not
     * between 1 and 12 (inclusive).
     */
    public static String intToMonthCompact(int month)
    {
        switch(month)
        {
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "Aug";
            case 9: return "Sept";
            case 10:return "Oct";
            case 11:return "Nov";
            case 12:return "Dec";
        }
        return null;
    }

    /**
     * Converts string of the form "YYYY-mm-DD" to a cleaner format,
     * where YYYY stands for the year, mm stands for the month and
     * DD stands for the day
     *
     * @param formattedDate formatted string to be converted
     * @return prettier format of string with full month name
     */
    public static String formattedDateToPrettyCompact(String formattedDate)
    {
        //Uses only first 10 characters in date
        formattedDate = formattedDate.substring(0,10);

        if (formattedDate.matches("\\d{4}-\\d{2}-\\d{2}"))
        {
            int month = Integer.parseInt(formattedDate.substring(5,7));
            String year = formattedDate.substring(0,4);
            String day = formattedDate.substring(8);

            return intToMonthCompact(month) + " " + day + ", " + year;
        }
        else
            throw new IllegalArgumentException("String must be formatted as YYYY-mm-DD");
    }

    /**
     * Converts a dp value to pixels
     * @param scale density of screen
     * @param dps value to be converted
     * @return result of conversion from dps to pixels
     */
    public static int getPixelsFromDP(float scale, int dps)
    {
        return (int)(dps * scale + 0.5f);
    }
}

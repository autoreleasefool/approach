package ca.josephroque.bowlingcompanion.data;

/**
 * Created by josephroque on 15-02-25.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.data
 * in project Bowling Companion
 */
public class ConvertValue
{

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

    public static String intToMonthExtended(int month)
    {
        switch(month)
        {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10:return "October";
            case 11:return "November";
            case 12:return "December";
        }
        return null;
    }

    public static int monthCompactToInt(String month)
    {
        if (month.startsWith("Jan"))
            return 1;
        else if (month.startsWith("Feb"))
            return 2;
        else if (month.startsWith("Mar"))
            return 3;
        else if (month.startsWith("Apr"))
            return 4;
        else if (month.startsWith("May"))
            return 5;
        else if (month.startsWith("Jun"))
            return 6;
        else if (month.startsWith("Jul"))
            return 7;
        else if (month.startsWith("Aug"))
            return 8;
        else if (month.startsWith("Sep"))
            return 9;
        else if (month.startsWith("Oct"))
            return 10;
        else if (month.startsWith("Nov"))
            return 11;
        else if (month.startsWith("Dec"))
            return 12;
        else
            return -1;
    }

    /**
     * Returns ending of an ordinal number which corresponds to a possible day
     * of the month.
     * @param ordinal ordinal ending of the day to get
     * @return ordinal ending corresponding to parameter
     */
    public static String endingOfOrdinalDay(int ordinal)
    {
        if (ordinal < 1 || ordinal > 31)
        {
            throw new IllegalArgumentException("Ordinal must be between 1 and 31");
        }

        switch(ordinal)
        {
            case 1:case 21:case 31:return "st";
            case 2:case 22: return "nd";
            case 3:case 23: return "rd";
            default: return "th";
        }
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
     * Converts string of the form "YYYY-mm-DD" to a cleaner format,
     * where YYYY stands for the year, mm stands for the month and
     * DD stands for the day
     *
     * @param formattedDate formatted string to be converted
     * @return prettier format of string with full month name
     */
    public static String formattedDateToPrettyExtended(String formattedDate)
    {
        if (formattedDate.matches("\\d{4}-\\d{2}-\\d{2}"))
        {
            int month = Integer.parseInt(formattedDate.substring(5,7));
            String year = formattedDate.substring(0,4);
            int day = Integer.parseInt(formattedDate.substring(8));

            return intToMonthExtended(month) + " " + day + endingOfOrdinalDay(day) + ", " + year;
        }
        else
            throw new IllegalArgumentException("String must be formatted as YYYY-mm-DD");
    }

    public static int[] prettyCompactToFormattedDate(String pretty)
    {
        int[] date = new int[3];
        if (pretty.matches("[A-Z]\\w{2,3} \\d{1,2}, \\d{4}"))
        {
            date[0] = monthCompactToInt(pretty.substring(0,3));
            switch(date[0])
            {
                case 1:case 2:case 3:case 4:case 5:case 8:case 10:case 11:case 12:
                    date[1] = Integer.parseInt(pretty.substring(4, pretty.indexOf(",")));
                    date[2] = Integer.parseInt(pretty.substring(pretty.indexOf(",")));
                    break;
                case 6:case 7:case 9:
                    date[1] = Integer.parseInt(pretty.substring(5, pretty.indexOf(",")));
                    date[2] = Integer.parseInt(pretty.substring(pretty.indexOf(",")));
                    break;
            }
        }
        else
            throw new IllegalArgumentException("String must match the pattern: [A-Z]\\w{2,3} \\d{1,2}, \\d{4}");
        return date;
    }
}
